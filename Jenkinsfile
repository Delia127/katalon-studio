node {
    stage('Prepare'){
	// Start neccessary services to prepare required libraries if needed
	// deleteDir()
        build job: 'StartServices'
    }
    stage('Check out') {
	    retry(3){
        	checkout scm
	    }
    }/Users/nguyenvinh/katalon/source/com.kms.katalon
    stage('Build') {
	    env.WORKSPACE = pwd()
	    sub = { it.split("1=")[1] }
	    def versionContent = readFile "${env.WORKSPACE}/source/com.kms.katalon/about.mappings"
	    def version = sub(versionContent)		    
	    
	// FIXME: Use full mvn patch due to mvn command not found issue - no idea why
    	if (env.BRANCH_NAME.findAll(/^[Release]+/)) {
    		sh '''
		    cd source
		    sudo /usr/local/bin/mvn clean verify -Pprod
	        '''
    	} else {
    		sh '''
		    cd source
		    sudo /usr/local/bin/mvn clean verify -Pstag
	        '''
    	}       
   }
    stage('Package') {
        sh '''
            sudo ./package.sh ${JOB_BASE_NAME} ${BUILD_ID} 
        '''

        if (env.BRANCH_NAME == 'release') {
                sh '''
                    sudo ./verify.sh ${JOB_BASE_NAME} ${BUILD_ID} 
                '''
        }
    }
}
