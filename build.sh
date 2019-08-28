#!/usr/bin/env bash

set -xe

prepare() {
    pip3 install pyjavaproperties
    mkdir -p ${tmpDir}
}

get_version() {
    cd $katalonDir
    python3 get_version.py $branch
    source ./variable.sh
}

generate_links_file() {
    cd $katalonDir
    python3 generate_links_file.py "${tmpDir}/links.txt" $version $tag $isBeta
}

generate_lastest_release_file() {
    cd $katalonDir
    python3 generate_lastest_release_file.py "${tmpDir}/lastest_release.json" $version
}

generate_release_json_file() {
    cd $katalonDir
    python3 generate_release_json_file.py "${tmpDir}/releases.json" $version
}

generate_latest_version_json_file() {
    cd $katalonDir
    python3 generate_latest_version_json_file.py "${tmpDir}/latest_version.json" $version
}

building() {
    ulimit -c unlimited

    cd $katalonDir/source && mvn ${mavenOpts} -N io.takari:maven:wrapper -Dmaven=$mavenVersion
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

    if [ "$isQtest" = "true" ]
    then
        echo "Building: qTest Prod"
        cd $katalonDir/source && $katalonDir/source/mvnw ${mavenOpts} -pl '!com.kms.katalon.product' clean verify -P prod
    else
        echo "Building: Standard Prod"
        cd $katalonDir/source && $katalonDir/source/mvnw ${mavenOpts} -pl '!com.kms.katalon.product.qtest_edition' clean verify -P prod
    fi

    cd $katalonDir/source/com.kms.katalon.apidocs && $katalonDir/source/mvnw ${mavenOpts} clean verify && cp -R 'target/resources/apidocs' ${tmpDir}
}

copy_build() {
    if [ "$isQtest" = "false" ]
    then
        # cd com.kms.katalon.product.product/macosx/cocoa/x86_64 && cp -R 'Katalon Studio.app' ${tmpDir}
        cd $katalonDir/source/com.kms.katalon.product/target/products
        cp *.zip ${tmpDir}
        cp *.tar.gz ${tmpDir}
    fi

    if [ "$isQtest" = "true" ]
    then
        # cd com.kms.katalon.product.qtest_edition.product/macosx/cocoa/x86_64 && cp -R 'Katalon Studio.app' ${tmpDir}
        cd $katalonDir/source/com.kms.katalon.product.qtest_edition/target/products
        cp *.zip ${tmpDir}
        cp *.tar.gz ${tmpDir}
    fi
}

generate_update_package() {
    cd $katalonDir
    if [ "$withUpdate" = "true" ]
    then
        cd tools/updater
        python3 $katalonDir/generate_scan_info_file.py "scan_info.json" $version "${katalonDir}/source/com.kms.katalon.product/target/products/com.kms.katalon.product.product" "${tmpDir}/update"
        java -jar json-map-builder-1.0.0.jar
    fi
}

repackage() {
    cd $katalonDir
    cd tools/repackage
    npm prune && npm install
    node repackage.js ${tmpDir}/Katalon_Studio_Windows_32.zip ${version}
    node repackage.js ${tmpDir}/Katalon_Studio_Windows_64.zip ${version}
    node repackage.js ${tmpDir}/Katalon_Studio_Linux_64.tar.gz ${version}

    rm -rf ${tmpDir}/*.zip
    rm -rf ${tmpDir}/*.tar.gz
    mv ${tmpDir}/output/*.zip ${tmpDir}/
    mv ${tmpDir}/output/*.tar.gz ${tmpDir}/
    rm -rf ${tmpDir}/output
    cd '${tmpDir}' && zip -r '${tmpDir}/Katalon Studio.app.zip' 'Katalon Studio.app'

    rm -rf '${tmpDir}/Katalon Studio.app'

    cd '${tmpDir}' && zip -r '${tmpDir}/apidocs.zip' 'apidocs'
    rm -rf '${tmpDir}/apidocs'
}


branch=${0}
tmpDir=${1}
katalonDir=${2}
mavenOpts=${3}

echo $branch
echo $tmpDir
echo $katalonDir
echo $mavenOpts

mavenVersion=3.5.4

prepare
get_version
generate_links_file
generate_lastest_release_file
generate_release_json_file
generate_latest_version_json_file
building
copy_build
generate_update_package
repackage