pipeline {
    agent any

    tools {
        maven 'default'
    }

    stages {
        stage('Checkout') {
            // checkout code
            options {
                retry(3)
            }
            steps {
                checkout scm
            }
        }


        stage('Set permissions to source') {
            // set write permissions to current workspace
            steps {
                sh '''sudo chmod -R 777 ${WORKSPACE}'''
            }
        }

        stage('Setup Maven repositories') {
            // start maven commands to get dependencies
            steps {
                dir("tools") {
                    sh '''  ./stop-dev-server.sh
                            ./start-dev-server.sh
                       '''
                }
            }
        }

        stage('Compile & Build') {
            // generate Katalon builds
            steps {
                echo "Maven version is:"
                sh "mvn --version"
                echo "Wait 60 seconds to start maven services successfully"
                sleep 60
                script {
                    dir("source") {
                        if (BRANCH_NAME.findAll(/^[release]+/)) {
                            sh ''' mvn clean verify -P prod '''
                        } else {
                            sh ''' mvn clean verify -P dev '''
                        }
                    }
                }
            }
        }

        stage('Copy builds') {
            // copy generated builds and changelogs to shared folder on server
            steps {
                dir("source/com.kms.katalon.product/target/products") {
                    script {
                        String tmpDir = "/tmp/katabuild/${BRANCH_NAME}_${BUILD_TIMESTAMP}"
                        writeFile(encoding: 'UTF-8', file: "${tmpDir}/${BRANCH_NAME}_${BUILD_TIMESTAMP}_changeLogs.txt", text: getChangeString())
                        // copy builds, require https://wiki.jenkins.io/display/JENKINS/File+Operations+Plugin
                        fileOperations([
                                fileCopyOperation(
                                        excludes: '',
                                        includes: '*.zip, *.tar.gz',
                                        flattenFiles: true,
                                        targetLocation: "${tmpDir}")
                        ])
                    }
                }
            }
        }

        stage ('Success') {
            steps {
                script {
                    currentBuild.result = 'SUCCESS'
                }
            }
        }
    }

    post {
        always {
            mail(
                    from: 'build-ci@katalon.com',
                    replyTo: 'build-ci@katalon.com',
                    to: "qa@katalon.com",
                    subject: "Build $BUILD_NUMBER - " + currentBuild.currentResult + " ($JOB_NAME)",
                    body: "Changes:\n " + getChangeString() + "\n\n Check console output at: $BUILD_URL/console" + "\n"
            )
        }
    }

    // configure Pipeline-specific options
    options {
        // keep only last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // timeout job after 60 minutes
        timeout(time: 30, unit: 'MINUTES')
        // wait 10 seconds before starting scheduled build
        quietPeriod 10
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
