#! /bin/sh

currentscript=$0

if [ "$(which git)" = "" ];
then
  echo "Install git first"
  exit 0
fi

git clone https://gitlab.com/tyrkinn/done-cli/

cd done-cli

bbpath=$(which bb)

if [[ "$bbpath" = "" ]] || [[ "$bbpath" = "bb not found" ]];
then
  echo "Installing babashka\n"
  curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
  chmod +x install
  ./install
  rm -rf ./install
fi

cp ./done\!.clj /usr/local/bin/done\!

cd ..

rm -rf done-cli

echo "Done!-cli successfully moved to bin.\nType \`done!\` to see usage string"

function finish {
  shred -u ${currentscript};
}

trap finish EXIT
