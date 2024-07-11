def call(Map pipeline_param){
    def agents_online = [];
    
    pipeline {
        agent none
        
        options { 
            skipDefaultCheckout() 
        }

        parameters {
            activeChoice choiceType: 'PT_SINGLE_SELECT', 
            filterLength: 1, filterable: false,
            name: 'GROUP_WIN_AGENTS',
            script: groovyScript(fallbackScript: [classpath: [], oldScript: '', sandbox: false, script: ''], 
            script: [classpath: [], oldScript: '', sandbox: false, 
            script: 'return [\'windows_agents\']'])

            reactiveChoice choiceType: 'PT_CHECKBOX', filterLength: 1, filterable: false, 
            name: 'WIN_AGENTS', referencedParameters: 'GROUP_WIN_AGENTS', 
            script: groovyScript(fallbackScript: [classpath: [], oldScript: '', sandbox: false, script: ''], 
            script: [classpath: [], oldScript: '', sandbox: false, 
            script: '''
            def GetNodesByLabel(String label){
                def nodes = []
                jenkins.model.Jenkins.get().computers.each { c ->
                if (c.node.labelString.contains("${label}")) {
                    nodes.add(c.node.selfLabel.name)
                }}
                return nodes
            }

            if (GROUP_WIN_AGENTS.equals("windows_agents")) {
                return GetNodesByLabel(\'windows_agents\')
            }
            '''])
        }

        stages {
            stage('Ping agent'){
                steps{
                    script {
                        if(params.WIN_AGENTS.isEmpty()){
                            error("Select at least one host")
                        } else {
                            agents_online = params.WIN_AGENTS.split(',')
                            for (item in agents_online) {
                                CheckAgent(item)
                            }
                        }   
                    }
                }
            }

            stage('Checkout git'){
                steps {
                    git branch: pipeline_param.git_branch, 
                    poll: false,
                    credentialsId: pipeline_param.git_cred_id,
                    url: pipeline_param.git_repo_url

                    stash includes: pipeline_param.stash_includes, excludes: pipeline_param.stash_excludes, name: 'src'
                }
            }

            stage('Deploy'){
                steps {
                    script {
                        def tasks = [:]
                        for (item in agents_online){
                            def label = item
                            tasks[label] = UnstashOnAgent(label, pipeline_param.command_deploy, pipeline_param.func_deploy)
                        }

                        parallel tasks
                    }
                }
            }
        }
    }
}