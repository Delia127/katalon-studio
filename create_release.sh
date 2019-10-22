#!/usr/bin/env bash

set -xe

create_github_release() {
    if [ "$isRelease" = "true" -a "$isQtest" = "false" ]
    then
        cd $katalonDir/tools/release
        npm prune && npm install
        node app.js ${github_token} v${tag} \
            "${tmpDir}/lastest_release.json" \
            "${tmpDir}/latest_version.json" \
            "${tmpDir}/releases.json" \
            "${tmpDir}/apidocs.zip" \
            "${tmpDir}/commit.txt" \
            "${tmpDir}/Katalon Studio.app.zip" \
            "${tmpDir}/Katalon Studio.dmg" \
            "${tmpDir}/Katalon_Studio_Linux_64-${version}.tar.gz" \
            "${tmpDir}/Katalon_Studio_Windows_32-${version}.zip" \
            "${tmpDir}/Katalon_Studio_Windows_64-${version}.zip" \
            "${tmpDir}/Katalon_Studio_Engine_MacOS-${version}.tar.gz" \
            "${tmpDir}/Katalon_Studio_Engine_Linux_64-${version}.tar.gz" \
            "${tmpDir}/Katalon_Studio_Engine_Windows_32-${version}.zip" \
            "${tmpDir}/Katalon_Studio_Engine_Windows_64-${version}.zip"
    fi         
}

source ./variable.sh 

katalonDir=${1}
tmpDir=${2}
github_token=${3}

create_github_release
