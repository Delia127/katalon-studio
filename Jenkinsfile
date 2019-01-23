#!/usr/bin/env bash

import hudson.model.Result
import hudson.model.Run
import jenkins.model.CauseOfInterruption.UserInterruption

pipeline {
    agent any
   
    tools {
        maven 'default'
    }
    
    environment {
        tmpDir = "/tmp/katabuild/${BRANCH_NAME}_${BUILD_TIMESTAMP}"
    }
    
    stages {
       
        stage('Building') {
                // Start maven commands to get dependencies
            steps {
                    script {
                        dir("source") {
                            sh 'echo "Building Stage"'
                // Generate Katalon builds   
                // If branch name contains "release", build production mode for non-qTest package
                // else build development mode for qTest package    
                            if (BRANCH_NAME ==~ /.*release.*/) {
                                if (BRANCH_NAME ==~ /.*qtest.*/) {
                                    sh 'echo "Building: qTest Prod"'
                                } else {
                                    sh 'echo "Building: Standard Prod"'
                                }
                            } else {      
                                sh 'echo "Building: qTest Dev"'
                            }
                        }
                    }
                }
            }
        }
        
        /*
        stage('Testing') {
            steps {
                dir ("source/com.kms.katalon.product.qtest_edition/target/products/com.kms.katalon.product.qtest_edition.product/macosx/cocoa/x86_64")
                {
                    sh 'curl -O https://github.com/katalon-studio/katalon-keyword-tests/archive/master.zip'
                    sh 'unzip master.zip'
                    sh './Katalon\\ Studio.app/Contents/MacOS/katalon -noSplash  -runMode=console -projectPath="katalon-keyword-tests/katalon-keyword-tests.prj" -retry=0 -testSuiteCollectionPath="Test Suites/All Tests -browserType=Chrome (headless)"'
                }
            }
        }
        */ 
        
        

        stage ('Success') {
            steps {
                script {
                    // Mark status for current job
                    currentBuild.result = 'SUCCESS'
                }
            }
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
