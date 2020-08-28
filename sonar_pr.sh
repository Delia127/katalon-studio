set -xe

building() {
    cd $katalonDir/source && mvn ${mavenOpts} -N io.takari:maven:wrapper -Dmaven=$mavenVersion

    ulimit -c unlimited

    cd $katalonDir/source/com.kms.katalon.repo && $katalonDir/source/mvnw ${mavenOpts} p2:site 
    
    cd $katalonDir/source/com.kms.katalon.repo && nohup $katalonDir/source/mvnw ${mavenOpts} -Djetty.port=9999 jetty:run > /tmp/9999.log &
    until $(curl --output /dev/null --silent --head --fail http://localhost:9999/site); do
        printf '.'
        cat /tmp/9999.log
        sleep 5
    done

    cd $katalonDir/source/com.kms.katalon.p2site && nohup $katalonDir/source/mvnw ${mavenOpts} -Djetty.port=33333 jetty:run > /tmp/33333.log &           
    until $(curl --output /dev/null --silent --head --fail http://localhost:33333/site); do
        printf '.'
        cat /tmp/33333.log
        sleep 5
    done

    echo "Building: Sonnar Scanner for PR"
    cd $katalonDir/source && $katalonDir/source/mvnw ${mavenOpts} verify sonar:sonar -Dsonar.host.url=https://sonarcloud.io/ -Dsonar.login=0004a33f837ce5070e70ec09e251fd37dd93975b -Dsonar.projectKey=kms-technology_katalon -Dsonar.organization=kms-technology -Dsonar.pullrequest.key=$prKey -Dsonar.pullrequest.branch=$prSource -Dsonar.pullrequest.base=$prTarget
}

branch=${1}
tmpDir=${2}
katalonDir=${3}
mavenOpts=${4}
commitId=${5}
prSource=${6}
prKey=${7}
prTarget=${8}

mavenVersion=3.5.4

printenv

building

cd ${tmpDir}
ls -al