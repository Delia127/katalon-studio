node {
    stage('Check out') {
	sh '''
	    if [ ! -f "Jenkinsfile" ]; then
                cp -rf "/Users/katalon/Katalon Studio/katalon/" . | true
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
    stage('Package') {
        sh '''
            sudo ./package.sh
        '''
    }
}
