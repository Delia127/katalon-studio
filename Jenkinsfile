node {
    stage('Check out') {
        checkout scm
    }  
    stage('Build') {    
    	if (env.BRANCH_NAME == 'release') {
    		sh '''
		    executing_maven=eval "which mvn"
		    executing_maven="$executing_maven clean verify -Pprod"
	            cd source
	            eval $executing_maven
	        '''
    	} else {
    		sh '''
		    executing_maven=eval "which mvn"
		    executing_maven="$executing_maven clean verify"
	            cd source
	            eval $executing_maven
	        '''
    	}       
    }
}
