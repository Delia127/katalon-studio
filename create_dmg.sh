#!/bin/bash

set -xe

ulimit -c unlimited
KATABUILD="${1}"

npm config delete prefix
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && chmod +x "$NVM_DIR/nvm.sh" && \. "$NVM_DIR/nvm.sh"
[ -s "$NVM_DIR/bash_completion" ] && chmod +x "$NVM_DIR/bash_completion" && \. "$NVM_DIR/bash_completion"

nvm install 10.15.3
nvm use 10.15.3
npm install --global create-dmg

#Create .dmg file
cd $KATABUILD
sudo codesign --verbose --force --deep --sign "80166EC5AD274586C44BD6EE7A59F016E1AB00E4" --timestamp=none "Katalon Studio.app"
sudo create-dmg "Katalon Studio.app"
mv 'Katalon 1.0.0.dmg' 'Katalon Studio.dmg'
echo "Packaging DMG ... Done"