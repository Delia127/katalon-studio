#!/usr/bin/env bash
testProjectDir="$1"

cd "$testProjectDir"
  
wget -O web-test.zip https://github.com/katalon-studio-samples/web-samples/archive/master.zip

mkdir web-test
unzip web-test.zip -d web-test

cp -R web-test/web-samples-master/ "$testProjectDir"

rm web-test.zip
rm -rf web-test