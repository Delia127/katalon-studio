#!/usr/bin/env bash

set -xe

create_github_release() {
    if [ "$isRelease" = "true" -a "$isQtest" = "false" ]
    then
        cd $katalonDir/tools/release
        npm prune && npm install
        node app.js ${github_token} v${tag} \
            '${tmpDir}/lastest_release.json' \
            '${tmpDir}/latest_version.json' \
            '${tmpDir}/releases.json'
    fi         
}

source ./variable.sh 

katalonDir=${1}
github_token=${2}

create_github_release