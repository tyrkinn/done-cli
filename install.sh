#! /bin/sh

bbpath=$(which bb)

if [ $bbpath = "bb not found" ];
then
  echo "Installing babashka\n"
  curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
  chmod +x install
  ./install
  rm -rf ./install
fi

cp ./done\!.clj /usr/local/bin/done\!

echo "Done!-cli successfully moved to bin.\nType \`done!\` to see usage string"

