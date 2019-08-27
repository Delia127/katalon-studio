#!/usr/bin/env bash

set -xe

echo  ${MAVEN_OPTS}

# ulimit -c unlimited

# cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.repo && mvn ${MAVEN_OPTS} p2:site 
# cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.repo && nohup mvn ${MAVEN_OPTS} -Djetty.port=9999 jetty:run > /tmp/9999.log &

# until $(curl --output /dev/null --silent --head --fail http://localhost:9999/site); do
#     printf '.'
#     cat /tmp/9999.log
#     sleep 5
# done

# cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.p2site && nohup mvn -Djetty.port=33333 jetty:run > /tmp/33333.log &
                    
# until $(curl --output /dev/null --silent --head --fail http://localhost:33333/site); do
#     printf '.'
#     cat /tmp/33333.log
#     sleep 5
# done

# cd $BUILD_REPOSITORY_LOCALPATH/source && mvn -pl \\!com.kms.katalon.product.qtest_edition clean verify -P prod

# cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.apidocs && mvn clean ${command} && cp -R 'target/resources/apidocs' '/tmp/katabuild'
                 
