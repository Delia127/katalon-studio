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
}

