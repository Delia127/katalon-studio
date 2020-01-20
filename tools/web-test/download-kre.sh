kreDownloadLink="https://github.com/katalon-studio/katalon-studio/releases/download/v7.2.3/Katalon_Studio_Engine_MacOS-7.2.3.tar.gz"
kreDir="$1"

cd "$kreDir"

curl "$kreDownloadLink" -o "katalon_runtime_engine.tar.gz" -L 

tar -xzvf katalon_runtime_engine.tar.gz

rm katalon_runtime_engine.tar.gz

cp -R Katalon_Studio_Engine_MacOS-7.2.3/ "$kreDir"

rm -rf Katalon_Studio_Engine_MacOS-7.2.3