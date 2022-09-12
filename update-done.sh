#! /bin/bash

echo "Updating done cli ..."
curl https://gitlab.com/tyrkinn/done-cli/-/raw/main/done!.clj > /usr/local/bin/done!
echo "New your done! up to date!"
