node {
    stage('Check out') {
        checkout scm
    }  
    stage('Build') {    
    	if (env.BRANCH_NAME == 'release') {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    /usr/local/bin/mvn clean verify
	        '''
    	}       
    }
}
