#! /bin/sh

echo "Checking git..."

if [[ "$(which git)" = "" ]] || [[ "$(which git)" = "git not found" ]];
then
  echo "Install git first"
  exit 0
fi

echo "Cloning repo..."

git clone https://gitlab.com/tyrkinn/done-cli/

cd done-cli

bbpath=$(which bb)

echo "Checking babashka..."

if [[ "$bbpath" = "" ]] || [[ "$bbpath" = "bb not found" ]];
then
  echo "Installing babashka\n"
  curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install
  chmod +x install
  ./install
  rm -rf ./install
fi

echo "Moving done! to /usr/local/bin ..."

cp ./done\!.clj /usr/local/bin/done\!

echo "Moving update-done util to /usr/local/bin ..."

cp ./update-done.sh /usr/local/bin/update-done

echo "Giving execute permissons to update-done ..."

chmod +x /usr/local/bin/update-done

cd ..

echo "Removing repo"

rm -rf done-cli

echo -e "\nDone!-cli successfully moved to /usr/local/bin.\n\nType \`done!\` to see usage string\n"

echo "Cleanup ..."

function finish {
  shred -u install.sh;
}

trap finish EXIT
