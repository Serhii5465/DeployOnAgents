def call(platform, label, command_deploy, func_deploy){
    return {
        node(label){
            stage('Unstash on ' + label){
                script{
                    unstash 'src'

                    if (command_deploy && platform == 'win32'){
                        bat returnStatus: true, script: command_deploy
                    }

                    if(command_deploy && platform == 'linux'){
                        sh returnStatus: true, script: command_deploy
                    }

                    if (func_deploy && platform == 'win32'){
                        func_deploy(label)
                    }
                }
            }
        }
    }
}