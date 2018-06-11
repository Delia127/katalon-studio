node {
    stage('Check out') {
	sh '''
	    if [ ! -f "Jenkinsfile" ]; then
                cp -r "/Users/katalon/Katalon Studio/katalon/" .
            fi
	''' 
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
