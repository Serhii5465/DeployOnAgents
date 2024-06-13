def call(label, command){
    return {
        node(label){
            stage('Unstash on ' + label){
                script{
                    unstash 'src'
                    if(command){
                        // bat returnStatus: true, script: command
                        println command
                    } else {
                        DeployPowershellArtifacts(label)
                    }
                }
            }
        }
    }
}