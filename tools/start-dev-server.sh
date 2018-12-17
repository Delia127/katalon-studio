SCRIPTDIR=`cd "$(dirname "$0")" && pwd`
osascript -e "tell application \"Terminal\" to do script \"cd ${SCRIPTDIR}; cd ../source/com.kms.katalon.repo; mvn clean p2:site; mvn -Djetty.port=9999 jetty:run\""
osascript -e "tell application \"Terminal\" to do script \"cd ${SCRIPTDIR}; cd ../source/com.kms.katalon.p2site; mvn -Djetty.port=33333 clean jetty:run\""
