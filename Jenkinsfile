#!/usr/bin/env bash

import hudson.model.Result
import hudson.model.Run
import jenkins.model.CauseOfInterruption.UserInterruption
import groovy.json.JsonOutput

def version
def isRelease
def isBeta
def titleVersion
def tag

pipeline {
    agent any

    tools {
        maven 'default'
    }

    environment {
        tmpDir = "/tmp/katabuild/${BRANCH_NAME}_${BUILD_TIMESTAMP}"
    }

    stages {

        stage('Prepare') {
            steps {
                script {
                    // Terminate running builds of the same job
                    abortPreviousBuilds()
                    sh "mkdir -p ${env.tmpDir}"
                    sh 'chmod -R 777 ${WORKSPACE}'
                }
            }
        }

        stage('Get version') {
            steps {
                script {
                    Properties properties = new Properties()
                    File propertiesFile = new File("${env.WORKSPACE}/source/com.kms.katalon/about.mappings")
                    properties.load(propertiesFile.newDataInputStream())
                    version = properties.'1'
                    println("Version ${version}.")

                    def branch = env.BRANCH_NAME
                    println("Branch ${branch}.")

                    if (branch.contains('issue3542')) {
                        branch = 'release-7.0.0.rc3'
                    }

                    if (!(branch.endsWith(version) || branch.contains("${version}.rc"))) {
                        println 'Branch or version is incorrect.'
                        throw new IllegalStateException('Branch or version is incorrect.')
                    }

                    isRelease = branch.startsWith('release-') || branch.contains('-release-')
                    println("Is release ${isRelease}.")

                    isBeta = isRelease && branch.contains('.rc')
                    println("Is beta ${isBeta}.")

                    withUpdate = isRelease && !isQtest && !isBeta
                    println("With update ${withUpdate}.")

                    if (isRelease) {
                        tag = branch.replace('release-', '')
                    } else {
                        tag = "${version}.DEV"
                    }
                    println("Tag ${tag}.")
                }

                dir('source/com.kms.katalon') {
                    script {
                        def commitId = sh(returnStdout: true, script: 'git rev-parse --short HEAD')
                        titleVersion = "${tag}-${commitId}"
                        def versionMapping = readFile(encoding: 'UTF-8', file: 'about.mappings')
                        versionMapping = versionMapping.replaceAll(/3=.*/, "3=${titleVersion}")
                        writeFile(encoding: 'UTF-8', file: 'about.mappings', text: versionMapping)
                    }
                }
            }
        }

        stage('Generate links.txt') {
            steps {
                script {
                    def releaseBeta = (isBeta) ? "release-beta/" : "";
                    def firstArg = (isBeta) ? tag : version;
                    def secondArg = version;
/* temporarily disable dmg
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon+Studio.dmg
*/
                    def templateString = """
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon+Studio.app.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Linux_64-${secondArg}.tar.gz
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Windows_32-${secondArg}.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Windows_64-${secondArg}.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Engine_MacOS-${secondArg}.tar.gz
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Engine_Linux_64-${secondArg}.tar.gz
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Engine_Windows_32-${secondArg}.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/Katalon_Studio_Engine_Windows_64-${secondArg}.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/apidocs.zip
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/changeLogs.txt
https://s3.amazonaws.com/katalon/${releaseBeta}${firstArg}/commit.txt
                     """;
                    writeFile(encoding: 'UTF-8', file: "${env.tmpDir}/links.txt", text: templateString)
                    def links_from_file = readFile(file: "${env.tmpDir}/links.txt")
                    println(links_from_file)
                }
            }
        }

        stage('Generate lastest_release.json') {
            steps {
                script {
                        def latestRelease =
"""[
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_32-${version}.zip",
        "file": "win_32"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Windows_64-${version}.zip",
        "file": "win_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon%20Studio.dmg",
        "file": "mac_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Linux_64-${version}.tar.gz",
        "file": "linux_64"
    }
]"""
                        writeFile(file: "${env.tmpDir}/lastest_release.json", text: latestRelease)
                        def latest_release_from_file = readFile(file: "${env.tmpDir}/lastest_release.json")
                        println(latest_release_from_file)

                }
            }
        }

        stage('Generate lastest_engine.json') {
            steps {
                script {
                        def latestRelease =
"""[
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Windows_32-${version}.zip",
        "file": "win_32"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Windows_64-${version}.zip",
        "file": "win_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_MacOS-${version}.tar.gz",
        "file": "mac_64"
    },
    {
        "location": "https://download.katalon.com/${version}/Katalon_Studio_Engine_Linux_64-${version}.tar.gz",
        "file": "linux_64"
    }
]"""
                        writeFile(file: "${env.tmpDir}/lastest_engine.json", text: latestRelease)
                        def latest_release_from_file = readFile(file: "${env.tmpDir}/lastest_engine.json")
                        println(latest_release_from_file)

                }
            }
        }

        stage('Generate releases.json') {
            steps {
                script {
                        def releases =
"""
    {
        "os": "macOS (app)",
        "version": "${version}",
        "filename": "Katalon.Studio.app.zip",
        "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Engine_MacOS-${version}.tar.gz"
    },
    {
        "os": "Linux",
        "version": "${version}",
        "filename": "Katalon_Studio_Linux_64-${version}.tar.gz",
        "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Engine_Linux_64-${version}.tar.gz"
    },
    {
        "os": "Windows 32",
        "version": "${version}",
        "filename": "Katalon_Studio_Windows_32-${version}.zip",
        "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Engine_Windows_32-${version}.zip"
    },
    {
        "os": "Windows 64",
        "version": "${version}",
        "filename": "Katalon_Studio_Windows_64-${version}.zip",
        "url": "https://github.com/katalon-studio/katalon-studio/releases/download/v${version}/Katalon_Studio_Engine_Windows_64-${version}.zip"
    },
"""
                        writeFile(file: "${env.tmpDir}/releases.json", text: releases)
                        def releases_from_file = readFile(file: "${env.tmpDir}/releases.json")
                        println(releases_from_file)

                }
            }
        }

        stage('Generate latest_version.json') {
            steps {
                script {
                    def latest_release = """
{
    "latestVersion": "${version}",
    "newMechanism": true,
    "latestUpdateLocation": "https://katalon.s3.amazonaws.com/update/${version}",
    "releaseNotesLink": "https://docs.katalon.com/katalon-studio/new/index.html",
    "quickRelease": true
}
                    """
                        writeFile(file: "${env.tmpDir}/latest_version.json", text: latest_release)
                        def latest_releases_from_file = readFile(file: "${env.tmpDir}/latest_version.json")
                        println(latest_releases_from_file)
                }
            }
        }

        stage('Building') {
            // Start maven commands to get dependencies
            steps {
                lock('p2:site') {
                    sh 'ulimit -c unlimited'
                    sh 'cd source/com.kms.katalon.repo && mvn p2:site'
                    sh 'cd source/com.kms.katalon.repo && nohup mvn -Djetty.port=9999 jetty:run > /tmp/9999.log &'
                    sh '''
                        until $(curl --output /dev/null --silent --head --fail http://localhost:9999/site); do
                            printf '.'
                            cat /tmp/9999.log
                            sleep 5
                        done
                    '''

                    sh 'cd source/com.kms.katalon.p2site && nohup mvn -Djetty.port=33333 jetty:run > /tmp/33333.log &'
                    sh '''
                        until $(curl --output /dev/null --silent --head --fail http://localhost:33333/site); do
                            printf '.'
                            cat /tmp/33333.log
                            sleep 5
                        done
                    '''

                    script {
                        dir("source") {
                            def command = isRelease ? 'verify' : 'verify'
                            // Generate Katalon builds
                            // If branch name contains "release", build production mode for non-qTest package
                            // else build development mode for qTest package
                            if (isQtest) {
                                echo "Building: qTest Prod"
                                sh "mvn clean ${command} -P prod"
                            } else {
                                echo "Building: Standard Prod"
                                sh "mvn clean ${command} -P prod"
                            }

                            // Generate API docs
                            sh "cd com.kms.katalon.apidocs && mvn clean ${command} && cp -R 'target/resources/apidocs' ${env.tmpDir}"
                        }
                    }
                }
            }
        }

        /*
        stage('Testing') {
            steps {
                dir ("source/com.kms.katalon.product/target/products/com.kms.katalon.product.product/macosx/cocoa/x86_64")
                {
                    sh 'curl -O https://github.com/katalon-studio/katalon-keyword-tests/archive/master.zip'
                    sh 'unzip master.zip'
                    sh './Katalon\\ Studio.app/Contents/MacOS/katalon -noSplash  -runMode=console -projectPath="katalon-keyword-tests/katalon-keyword-tests.prj" -retry=0 -testSuiteCollectionPath="Test Suites/All Tests -browserType=Chrome (headless)"'
                }
            }
        }
        */

        stage('Copy builds') {
            // Copy generated builds and changelogs to shared folder on server
            steps {
                dir("source/com.kms.katalon.product/target/products") {
                    script {
                        sh "cd com.kms.katalon.product.product/macosx/cocoa/x86_64 && cp -R 'Katalon Studio.app' ${env.tmpDir}"
                        writeFile(encoding: 'UTF-8', file: "${env.tmpDir}/changeLogs.txt", text: getChangeString())
                        writeFile(encoding: 'UTF-8', file: "${env.tmpDir}/commit.txt", text: "${GIT_COMMIT}")
                        fileOperations([
                                fileCopyOperation(
                                    excludes: '',
                                    includes: '*.zip, *.tar.gz, *.app',
                                    flattenFiles: true,
                                    targetLocation: "${env.tmpDir}")
                        ])
                    }
                }
            }
        }

        stage('Sign file') {
            steps {
                script {
                    // For release branches, execute codesign command to package .DMG file for macOS
                    sh "./codesign.sh ${env.tmpDir}"
                }
            }
        }

        // stage('Package .DMG file') {
        //     steps {
        //         lock('dropdmg') {
        //             script {
        //                 // For release branches, execute codesign command to package .DMG file for macOS
        //                 if (isRelease) {
        //                     sh "./dropdmg.sh ${env.tmpDir}"
        //                 }
        //             }
        //         }
        //     }
        // }

        stage('Generate update packages') {
            steps {
                script {
                    if (withUpdate) {
                        dir("tools/updater") {
                            def updateInfo = [
                                buildDir: "${WORKSPACE}/source/com.kms.katalon.product/target/products/com.kms.katalon.product.product",
                                destDir: "${tmpDir}/update",
                                version: "${version}"
                            ]
                            def json = JsonOutput.toJson(updateInfo)
                            json = JsonOutput.prettyPrint(json)
                            writeFile(file: 'scan_info.json', text: json)
                            sh 'java -jar json-map-builder-1.0.0.jar'
                        }
                    }
                }
            }
        }

        stage('Repackage') {
            steps {
                dir("tools/repackage") {
                    nodejs(nodeJSInstallationName: 'nodejs') {
                        sh 'npm prune && npm install'
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Windows_32.zip ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Windows_64.zip ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Linux_64.tar.gz ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Engine_Windows_32.zip ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Engine_Windows_64.zip ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Engine_MacOS.tar.gz ${version}"
                        sh "node repackage.js ${env.tmpDir}/Katalon_Studio_Engine_Linux_64.tar.gz ${version}"

                        sh "rm -rf ${env.tmpDir}/*.zip"
                        sh "rm -rf ${env.tmpDir}/*.tar.gz"
                        sh "mv ${env.tmpDir}/output/*.zip ${env.tmpDir}/"
                        sh "mv ${env.tmpDir}/output/*.tar.gz ${env.tmpDir}/"
                        sh "rm -rf ${env.tmpDir}/output"
                    }
                }
                sh "cd '${env.tmpDir}' && zip -r '${env.tmpDir}/Katalon Studio.app.zip' 'Katalon Studio.app'"
                sh "rm -rf '${env.tmpDir}/Katalon Studio.app'"

                sh "cd '${env.tmpDir}' && zip -r '${env.tmpDir}/apidocs.zip' 'apidocs'"
                sh "rm -rf '${env.tmpDir}/apidocs'"
            }
        }

        // stage('Upload update packages to S3') {
        //     steps {
        //         script {
        //             if (withUpdate) {
        //                 withAWS(region: 'us-east-1', credentials: 'katalon-deploy') {
        //                     s3Upload(file: "${env.tmpDir}/update/${tag}", bucket:'katalon', path: "update/${tag}", acl:'PublicRead')
        //                 }
        //                 sh "rm -rf '${env.tmpDir}/update'"
        //             }
        //         }
        //     }
        // }

        // stage('Upload build packages to S3') {
        //     steps {
        //         script {
        //             if (isRelease) {
        //                 def s3Location
        //                 if (isQtest) {
        //                     s3Location = "${tag}/qTest"
        //                 } else if (isBeta) {
        //                     s3Location = "release-beta/${tag}"
        //                 } else {
        //                     s3Location = "${tag}"
        //                 }
        //                 withAWS(region: 'us-east-1', credentials: 'katalon-deploy') {
        //                     s3Upload(file: "${env.tmpDir}", bucket:'katalon', path: "${s3Location}", acl:'PublicRead')
        //                 }
        //             }
        //         }
        //     }
        // }

//         stage('Create Github release') {
//             steps {
//                 script {
//                     if (isRelease && !isQtest) {
//                         dir("tools/release") {
//                             nodejs(nodeJSInstallationName: 'nodejs') {
//                                 sh 'npm prune && npm install'
//                                 withCredentials([string(credentialsId: 'github-token', variable: 'GITHUB_TOKEN')]) {
// /* temporarily disable dmg
//                                         '${env.tmpDir}/Katalon Studio.dmg' \
// */
//                                     sh """node app.js ${env.GITHUB_TOKEN} v${tag} \
//                                         '${env.tmpDir}/lastest_release.json' \
//                                         '${env.tmpDir}/latest_version.json' \
//                                         '${env.tmpDir}/releases.json' \
//                                         '${env.tmpDir}/apidocs.zip' \
//                                         '${env.tmpDir}/commit.txt' \
//                                         '${env.tmpDir}/Katalon Studio.app.zip' \
//                                         '${env.tmpDir}/Katalon_Studio_Linux_64-${version}.tar.gz' \
//                                         '${env.tmpDir}/Katalon_Studio_Windows_32-${version}.zip' \
//                                         '${env.tmpDir}/Katalon_Studio_Windows_64-${version}.zip'
//                                     """
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         }

        stage ('Success') {
            steps {
                script {
                    // Mark status for current job
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
    }

    // Send notification emails
    post {
        changed {
            emailext  body: "Changes:\n " + getChangeString() + "\n\n Check console output at: $BUILD_URL/console" + "\n",
                recipientProviders: [brokenBuildSuspects(), developers()],
                subject: "Build $BUILD_NUMBER - " + currentBuild.currentResult + " ($JOB_NAME)",
                attachLog: true
        }
        success {
            mail(
                    from: 'build-ci@katalon.com',
                    replyTo: 'build-ci@katalon.com',
                    to: "qa@katalon.com",
                    subject: "Build $BUILD_NUMBER - " + currentBuild.currentResult + " ($JOB_NAME)",
                    body: "Changes:\n " + getChangeString() + "\n\n Check console output at: $BUILD_URL/console" + "\n"
            )
        }
    }

    // Configure Pipeline-specific options
    options {
        // Keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Timeout job after 60 minutes
        timeout(time: 60, unit: 'MINUTES')
        // Wait 10 seconds before starting scheduled build
        quietPeriod 10
    }
}

def abortPreviousBuilds() {
    Run previousBuild = currentBuild.rawBuild.getPreviousBuildInProgress()

    while (previousBuild != null) {
        if (previousBuild.isInProgress()) {
            def executor = previousBuild.getExecutor()
            if (executor != null) {
                echo ">> Aborting older build #${previousBuild.number}"
                executor.interrupt(Result.ABORTED, new UserInterruption(
                    "Aborted by newer build #${currentBuild.number}"
                ))
            }
        }
        previousBuild = previousBuild.getPreviousBuildInProgress()
    }
}

@NonCPS
def getChangeString() {
    MAX_MSG_LEN = 100
    String changeString = ""
    echo "Gathering SCM changes"
    def changeLogSets = currentBuild.rawBuild.changeSets
    for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
        for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncated_msg = entry.msg.take(MAX_MSG_LEN)
            changeString += " - ${truncated_msg} [${entry.author}]\n"
        }
    }

    if (!changeString) {
        changeString = " - No new changes"
    }
    return changeString
}
