node {
    stage('Check out') {
        checkout scm
    }  
    stage('Prepare') {
        sh '''
            cd source
            mvn clean verify
        '''
    }
}


