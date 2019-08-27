#!/usr/bin/env bash

set -xe

ulimit -c unlimited

mavenVersion=3.5.4
cd $BUILD_REPOSITORY_LOCALPATH/source && mvn ${MAVEN_OPTS} -N io.takari:maven:wrapper -Dmaven=$mavenVersion

cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.repo && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} p2:site 
cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.repo && nohup $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -Djetty.port=9999 jetty:run > /tmp/9999.log &

until $(curl --output /dev/null --silent --head --fail http://localhost:9999/site); do
    printf '.'
    cat /tmp/9999.log
    sleep 5
done

cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.p2site && nohup $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -Djetty.port=33333 jetty:run > /tmp/33333.log &
                    
until $(curl --output /dev/null --silent --head --fail http://localhost:33333/site); do
    printf '.'
    cat /tmp/33333.log
    sleep 5
done

isQtest=true

if [ "$isQtest" = "true" ]
then
    echo "Building: qTest Prod"
    cd $BUILD_REPOSITORY_LOCALPATH/source && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -pl '!com.kms.katalon.product' clean verify -P prod
else
    echo "Building: Standard Prod"
    cd $BUILD_REPOSITORY_LOCALPATH/source && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -pl '!com.kms.katalon.product.qtest_edition' clean verify -P prod
fi

cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.product
ls -al
cd target/products

# cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.apidocs && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} clean ${command} && cp -R 'target/resources/apidocs' '/tmp/katabuild'              
