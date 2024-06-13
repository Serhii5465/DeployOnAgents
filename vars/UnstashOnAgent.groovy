def call(label, command){
    return {
        node(label){
            stage('Unstash on ' + label){
                script{
                    unstash 'src'
                    // bat returnStatus: true, script: command
                }
            }
        }
    }
}