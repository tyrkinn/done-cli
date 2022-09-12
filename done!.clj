#! /usr/bin/env bb

(require '[clojure.tools.cli :as cli]
         '[clojure.core.match :refer [match]]
         '[clojure.edn :refer [read-string]]
         '[babashka.fs :as fs])

(import 'java.time.LocalDateTime
        'java.time.format.DateTimeFormatter)

;; CONSTANTS

(def current-date
  "Current date in yyyy-MM-dd pattern"
  (.format
   (LocalDateTime/now)
   (DateTimeFormatter/ofPattern "yyyy-MM-dd")))

(def config-file-url
  "Configuration file path"
  (str (fs/home) "/" ".done.edn"))

(def log-file-url
  "Logging file path"
  (str (fs/home) "/" ".done-log.md"))

;; CONFIG RECORD

(defrecord Config [started done date])

;; CONFIG OPERATIONS

(def default-config
  "Default config to write when configuration first created"
  (Config. nil [] current-date))

(defn write-config
  "Writes Config struct into configuration file"
  [config config-file-url]
  (spit config-file-url (into {} config)))

(defn validate-config
  "Check if configuration file exists and writes default config into it if not"
  [default-config config-file-url]
  (when (not (fs/exists? config-file-url))
    (do (fs/create-file config-file-url)
        (write-config default-config config-file-url))))

(defn read-config
  "Reads Config struct from configuration file"
  [config-file-url]
  (map->Config (read-string (slurp config-file-url))))

(def file-conf
  "Config read from configuration file"
  (read-config config-file-url))

;; LOG OPERATIONS

(defn verify-log-file
  "Verifies if log file exists and creates new one if not"
  [log-file-url]
  (if (not (fs/exists? log-file-url))
    (fs/create-file log-file-url)))

(defn log-tasks-md
  "Writes current done tasks into log file"
  [{:keys [done date]} log-file-url]
  (verify-log-file log-file-url)
  (spit
   log-file-url
   (str "# Done tasks for: " date "\n"
        (str/join "\n" (map #(str "- " %) done)) "\n\n")
   :append true))

;; TASKS FUNCTIONS

(defn start-task
  "Sets new task to :started in config if it doesn't exists else print warning"
  [task-name conf config-file-url]
  (if (some? (:started conf))
    (println (str "You already started task named " (:started conf)))
    (let []
      (-> conf
          (assoc :started task-name)
          (write-config config-file-url))
      (println (str "Started task (" task-name ")")))))

(defn finish-task
  "Removes task from :started and moves it to :done if :started exists else print warning"
  [conf config-file-url]
  (if (nil? (:started conf))
    (println "Start some task first")
    (let [task-name (:started conf)]
      (-> conf
          (assoc :started nil)
          (assoc :done (conj (:done conf) task-name))
          (write-config config-file-url))
      (println (str "Task " task-name " successfully finished")))))

(defn abadon-task
  "Sets :started to nil if set and print warning message if not"
  [conf config-file-url]
  (if (nil? (:started conf))
    (println "Nothing to abadon")
    (do
      (-> conf
          (assoc :started nil)
          (write-config config-file-url))
      (println (str "You abadoned task " (:started conf))))))

(defn list-today-done
  "Lists :done from config"
  [conf]
  (if (empty? (:done conf))
    (println "Nothing done today((")
    (println (str "Done: \n\t"(str/join "\n\t" (:done conf))))))


(def usage-string
  "Cli usage/help string"
  (str
   "You should pass valid args:\n"
   "Usage:\n"
   "\tdone! start TASK_NAME - start task\n"
   "\tdone! finish - stops task\n"
   "\tdone! list - list tasks done today\n"
   "\tdone! abadon - abadon started task"))

(defn check-new-day
  "Checks if new day started, perform logging and updates current config"
  [conf current-date log-file-url config-file-url]
  (when (not (= (:date conf) current-date))
    (do (log-tasks-md conf log-file-url)
        (-> conf
            (assoc :date current-date)
            (assoc :done [])
            (write-config config-file-url)))))


;; MAIN FLOW

(validate-config default-config config-file-url)

(check-new-day file-conf current-date log-file-url config-file-url)

(let [args (vec (filter some? *command-line-args*))]
  (match args
         ["start" name] (start-task name file-conf config-file-url)
         ["finish"]     (finish-task file-conf config-file-url)
         ["list"]       (list-today-done file-conf)
         ["abadon"]     (abadon-task file-conf config-file-url)
         :else          (println usage-string)))
