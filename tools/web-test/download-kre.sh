kreDir="$1"
kreVersion="$2"

kreDownloadLink="https://download.katalon.com/${kreVersion}/Katalon_Studio_Engine_MacOS-${kreVersion}.tar.gz"

cd "$kreDir"

curl "$kreDownloadLink" -o "katalon_runtime_engine.tar.gz" -L 

tar -xzvf katalon_runtime_engine.tar.gz

rm katalon_runtime_engine.tar.gz

cp -R "Katalon_Studio_Engine_MacOS-${kreVersion}"/ "$kreDir"

rm -rf "Katalon_Studio_Engine_MacOS-${kreVersion}"