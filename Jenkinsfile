node {
    stage('Check out') {
	// FIXME: Workaround since checkout process in local is too slow 
	sh '''
	    if [ ! -f "Jenkinsfile" ]; then
                cp -r "/Users/katalon/Katalon Studio/katalon/" .
            fi
	''' 
        checkout scm
    }  
    stage('Build') {
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why    
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
