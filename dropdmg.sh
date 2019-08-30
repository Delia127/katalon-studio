#!/bin/bash
ulimit -c unlimited
KATABUILD="${1}"

nvm install 10.15.3
nvm use 10.15.3
npm install --global create-dmg

#Create .dmg file
cd $KATABUILD
sudo codesign --verbose --force --deep --sign "80166EC5AD274586C44BD6EE7A59F016E1AB00E4" --timestamp=none "Katalon Studio.app"
sudo create-dmg "Katalon Studio.app" "Katalon Studio"
echo "Packaging DMG ... Done"