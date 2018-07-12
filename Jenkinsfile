node {
    stage('Check out') {
	// FIXME: Workaround since checkout process in local is too slow 
	sh '''
	    echo $USER
	    if [ ! -f "Jenkinsfile" ]; then
                cp -rf "$HOME/katalon/" . | true
            fi
	''' 
        checkout scm
    }  
    stage('Build') {
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why    
    	if (env.BRANCH_NAME == 'release') {
    		sh '''
		    cd source
		    mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    mvn clean verify -Pstag
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
}
