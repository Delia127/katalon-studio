#!/bin/bash
ulimit -c unlimited
KATABUILD="${1}"

#Create .dmg file
cd $KATABUILD
sudo codesign --verbose --force --deep --sign "80166EC5AD274586C44BD6EE7A59F016E1AB00E4" --timestamp=none "Katalon Studio.app"
# sudo /usr/local/bin/dropdmg --config-name "Katalon Studio" "Katalon Studio.app"
sudo hdiutil create -srcfolder "Katalon Studio.app" "Katalon Studio"
echo "Packaging DMG ... Done"
