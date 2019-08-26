#!/usr/bin/env bash

set -xe

ulimit -c unlimited
cd source/com.kms.katalon.repo && mvn p2:site
cd source/com.kms.katalon.repo && nohup mvn -Djetty.port=9999 jetty:run > /tmp/9999.log &

until $(curl --output /dev/null --silent --head --fail http://localhost:9999/site); do
    printf '.'
    cat /tmp/9999.log
    sleep 5
done

cd source/com.kms.katalon.p2site && nohup mvn -Djetty.port=33333 jetty:run > /tmp/33333.log &
                    
until $(curl --output /dev/null --silent --head --fail http://localhost:33333/site); do
    printf '.'
    cat /tmp/33333.log
    sleep 5
done

command=verify

mvn -pl \\!com.kms.katalon.product.qtest_edition clean ${command} -P prod

cd com.kms.katalon.apidocs && mvn clean ${command} && cp -R 'target/resources/apidocs' '/tmp/katabuild'
                 
