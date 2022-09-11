#! /usr/bin/env bbwrap

(require '[clojure.tools.cli :as cli]
         '[clojure.core.match :refer [match]]
         '[clojure.edn :refer [read-string]]
         '[babashka.fs :as fs])

(import 'java.time.LocalDateTime
        'java.time.format.DateTimeFormatter)

(def now (LocalDateTime/now))

(def dt-formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd"))

(def cur (.format now dt-formatter))

(def config-file-url (str (fs/home) "/" ".done.edn"))

(defrecord Config [started done date])

(defn write-config [config]
  (spit config-file-url (into {} config)))

(defn write-default-config []
  (write-config (Config. nil [] cur)))

(defn read-config []
  (map->Config (read-string (slurp config-file-url))))

(defn start-task [task-name]
  (let [conf (read-config)]
    (if (some? (:started conf))
      (println (str "You already started task named " (:started conf)))
      (let []
        (-> conf
            (assoc :started task-name)
            write-config)
        (println (str "Started task (" task-name ")"))))))

(defn finish-task []
  (let [conf (read-config)]
    (if (nil? (:started conf))
      (println "Start some task first")
      (let [task-name (:started conf)]
        (-> conf
            (assoc :started nil)
            (assoc :done (conj (:done conf) task-name))
            write-config)
        (println (str "Task " task-name " successfully finished"))))))


(when (not (fs/exists? config-file-url))
  ((fs/create-file config-file-url)
   (write-config (Config. ))))


(def file-conf (read-config))

(let [conf-date (:date file-conf)]
  (when (not (= conf-date cur))
    (-> file-conf
        (assoc :date cur)
        (assoc :done [])
        write-config)))

(defn abadon-task []
  (let [conf (read-config)]
    (if (nil? (:started conf))
      (println "Nothing to abadon")
      (let [task-name (:started conf)]
        (-> conf
            (assoc :started nil)
            write-config)
        (println (str "You abadoned task " task-name))))))

(defn get-today-done []
  (let [conf (read-config)
        done-today (:done conf)]
    (if (empty? done-today)
      (println "Nothing done today((")
      (println (str "Done: \n\t"(str/join "\n\t" done-today))))))

(def usage-string (str
                   "You should pass valid args:\n"
                   "Usage:\n"
                   "\tdone! start TASK_NAME - start task\n"
                   "\tdone! finish - stops task\n"
                   "\tdone! today - list tasks done today\n"
                   "\tdone! abadon - abadon started task"))

(defn prepare-args []
  (let [[cmd options] (rest *command-line-args*)]
    (vec (filter some? [cmd options]))))

(let [args (prepare-args)]
  (match args
         ["start" name] (start-task name)
         ["finish"] (finish-task)
         ["today"] (get-today-done)
         ["abadon"] (abadon-task)
         :else (println usage-string)))
