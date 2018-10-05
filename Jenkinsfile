node {
    stage('Prepare'){
        build job: 'StartServices'
    }
    stage('Check out') {
	    retry(2){
        	checkout scm
	    }
    }
    stage('Build') {
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why
	// Start neccessary services to prepare required libraries if needed
	
    	if (env.BRANCH_NAME.findAll(/^[Release]+/)) {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify -Pstag
	        '''
    	}       
    }
    stage('Package') {
        sh '''
            sudo ./package.sh ${JOB_BASE_NAME}
        '''

        if (env.BRANCH_NAME == 'release') {
                sh '''
                    sudo ./verify.sh ${JOB_BASE_NAME}
                '''
        }
    }
     post {
        changed {
            script {
                if (currentBuild.currentResult == 'FAILURE') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: '$DEFAULT_SUBJECT',
                        body: '$DEFAULT_CONTENT',
                        recipientProviders: [
                            [$class: 'CulpritsRecipientProvider'],
                            [$class: 'DevelopersRecipientProvider'],
                            [$class: 'RequesterRecipientProvider']
                        ], 
                        replyTo: '$DEFAULT_REPLYTO',
                        to: '$DEFAULT_RECIPIENTS'
                }
            }
        }
    }
}
