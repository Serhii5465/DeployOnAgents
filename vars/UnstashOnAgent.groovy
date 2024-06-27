def call(label, command_deploy, func_deploy){
    return {
        node(label){
            stage('Unstash on ' + label){
                script{
                    unstash 'src'

                    if(command_deploy){
                        bat returnStatus: true, script: command_deploy
                    }

                    if(func_deploy){
                        func_deploy(label)
                    }
                }
            }
        }
    }
}