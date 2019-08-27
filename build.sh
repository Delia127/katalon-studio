#!/usr/bin/env bash

set -xe

BUILD_REPOSITORY_LOCALPATH='/Users/quiducle/Documents/Projects/katalon'
MAVEN_OPTS=''

mavenVersion=3.5.4
tmpDir="/tmp/katabuild"
version=6.3.3
isQtest=true
isRelease=true
withUpdate=true
tag="6.3.3"

# Building
ulimit -c unlimited

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

if [ "$isQtest" = "true" ]
then
    echo "Building: qTest Prod"
    cd $BUILD_REPOSITORY_LOCALPATH/source && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -pl '!com.kms.katalon.product' clean verify -P prod
else
    echo "Building: Standard Prod"
    cd $BUILD_REPOSITORY_LOCALPATH/source && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} -pl '!com.kms.katalon.product.qtest_edition' clean verify -P prod
fi

cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.apidocs && $BUILD_REPOSITORY_LOCALPATH/source/mvnw ${MAVEN_OPTS} clean verify && cp -R 'target/resources/apidocs' ${tmpDir}      

# Copy builds
cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.product/target/products
if [ "$isQtest" = "false" ]
then
    # cd com.kms.katalon.product.product/macosx/cocoa/x86_64 && cp -R 'Katalon Studio.app' ${tmpDir}
    cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.product/target/products
    cp *.zip ${tmpDir}
    cp *.tar.gz ${tmpDir}
fi

cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.product.qtest_edition/target/products
if [ "$isQtest" = "true" ]
then
    # cd com.kms.katalon.product.qtest_edition.product/macosx/cocoa/x86_64 && cp -R 'Katalon Studio.app' ${tmpDir}
    cd $BUILD_REPOSITORY_LOCALPATH/source/com.kms.katalon.product.qtest_edition/target/products
    cp *.zip ${tmpDir}
    cp *.tar.gz ${tmpDir}
fi

# Sign file
cd $BUILD_REPOSITORY_LOCALPATH
./codesign.sh $tmpDir

# # Package .DMG file
# cd $BUILD_REPOSITORY_LOCALPATH
# if [ "$isRelease" = "true" ]
# then
#     ./dropdmg.sh $tmpDir
# fi

# # Generate update packages
# cd $BUILD_REPOSITORY_LOCALPATH
# if [ "$withUpdate" = "true" ]
# then
#     cd tools/updater
#     def updateInfo = [
#         buildDir: "${WORKSPACE}/source/com.kms.katalon.product/target/products/com.kms.katalon.product.product",
#         destDir: "${tmpDir}/update",
#         version: "${version}"
#     ]
#     def json = JsonOutput.toJson(updateInfo)
#     json = JsonOutput.prettyPrint(json)
#     writeFile(file: 'scan_info.json', text: json)
#     java -jar json-map-builder-1.0.0.jar
# }
               

# # Repackage
# cd $BUILD_REPOSITORY_LOCALPATH
# cd tools/repackage
# npm prune && npm install
# node repackage.js ${tmpDir}/Katalon_Studio_Windows_32.zip ${version}
# node repackage.js ${tmpDir}/Katalon_Studio_Windows_64.zip ${version}
# node repackage.js ${tmpDir}/Katalon_Studio_Linux_64.tar.gz ${version}

# rm -rf ${tmpDir}/*.zip
# rm -rf ${tmpDir}/*.tar.gz
# mv ${tmpDir}/output/*.zip ${tmpDir}/
# mv ${tmpDir}/output/*.tar.gz ${tmpDir}/
# rm -rf ${env.tmpDir}/output
# cd '${tmpDir}' && zip -r '${tmpDir}/Katalon Studio.app.zip' 'Katalon Studio.app'

# rm -rf '${env.tmpDir}/Katalon Studio.app'

# cd '${tmpDir}' && zip -r '${tmpDir}/apidocs.zip' 'apidocs'
# rm -rf '${tmpDir}/apidocs'

# # Upload update packages to S3
# if [ "$withUpdate" = "true" ]
# then
#     withAWS(region: 'us-east-1', credentials: 'katalon-deploy') {
#         s3Upload(file: "${env.tmpDir}/update/${tag}", bucket:'katalon', path: "update/${tag}", acl:'PublicRead')
#     }
#     rm -rf '${env.tmpDir}/update'
# fi
        
# # Upload build packages to S3
# if [ "$isRelease" = "true" ]
# then
#     if [ "$isQtest" = "true" ]
#     then
#         s3Location = "${tag}/qTest"
#     elif [ "$isBeta" = "true" ]
#     then
#         s3Location = "release-beta/${tag}"
#     else
#         s3Location = "${tag}"
#     fi
#     withAWS(region: 'us-east-1', credentials: 'katalon-deploy') {
#         s3Upload(file: "${env.tmpDir}", bucket:'katalon', path: "${s3Location}", acl:'PublicRead')
# fi
      