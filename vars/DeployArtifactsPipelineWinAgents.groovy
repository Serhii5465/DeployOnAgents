def call(Map pipeline_param){
    def agents_online = [];
    def platform = pipeline_param.platform
    def label = (platform == 'win32') ? 'win32_agents_vb' : 'linux_agents_vb'

    pipeline {
        agent none
        
        options { 
            skipDefaultCheckout() 
        }

        parameters {
            activeChoice choiceType: 'PT_CHECKBOX', 
            description: 'Select agents to run the build', filterLength: 1, filterable: false, 
            name: 'NODES', script: groovyScript(fallbackScript: [classpath: [], oldScript: '', sandbox: false, script: ''], 
            script: [classpath: [], oldScript: '', sandbox: false, 
            script: """
                def GetNodesByLabel(String label){
                    def nodes = []

                    jenkins.model.Jenkins.get().computers.each { c ->
                        if (c.node.labelString.contains(label)) {
                            nodes.add(c.node.selfLabel.name)
                        }
                    }

                    return nodes
                }

                return GetNodesByLabel("${label}")
            """
            ])
        }

        stages {
            stage('Ping agent'){
                steps{
                    script {
                        if(params.NODES.isEmpty()){
                            error("Select at least one host")
                        } else {
                            agents_online = params.NODES.split(',')
                            for (item in agents_online) {
                                CheckAgent(item)
                            }
                        }   
                    }
                }
            }

            // stage('Checkout git'){
            //     steps {
            //         git branch: pipeline_param.git_branch, 
            //         poll: false,
            //         credentialsId: pipeline_param.git_cred_id,
            //         url: pipeline_param.git_repo_url

            //         stash includes: pipeline_param.stash_includes, excludes: pipeline_param.stash_excludes, name: 'src'
            //     }
            // }

            // stage('Deploy'){
            //     steps {
            //         script {
            //             def tasks = [:]
            //             for (item in agents_online){
            //                 def label = item
            //                 tasks[label] = UnstashOnAgent(label, pipeline_param.command_deploy, pipeline_param.func_deploy)
            //             }

            //             parallel tasks
            //         }
            //     }
            // }
        }
    }
}