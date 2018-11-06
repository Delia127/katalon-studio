node {
    stage('Prepare') {
	  build job: 'Sync-Repo'
	  build job: 'run-gradle'
    }
	
    stage('Check out') {
	    retry(3){
        	checkout scm
	    }
    }
	
    stage('Build') {
	sh '''
	  cd /Users/katalon/deploy-app
	  chmod 777 gradlew
	 ./gradlew changeVersion --info
	 '''
	    
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why
    	if (env.BRANCH_NAME.findAll(/^[release]+/)) {
    		sh '''
		    cd source
		    sudo /usr/local/bin/mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    sudo /usr/local/bin/mvn clean verify -Pdev
	        '''
    	}       
    }
	
    stage('Package') {
	sh '''
	  ./gradlew accessJenkinsChanges packageMac copyAndRename --info
	  '''
    }
    stage('Notify') {
	mail body: "Katalon Studio build is here: ${env.BUILD_URL}" ,
            from: 'build-ci@katalon.com',
            replyTo: 'build-ci@katalon.com',
            subject: "${JOB_NAME}' (${BUILD_NUMBER} info",
            to: 'qa@katalon.com'
    }
}
