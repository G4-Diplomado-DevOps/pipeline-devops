
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

    pipeline {
        agent any

        parameters { choice(name: 'TOOL', choices: ['gradle', 'maven'], description: '') }
        
        stages {
            stage('Pipeline') {
                steps {
                    script{

                        env.STAGE

                        if (params.TOOL == 'gradle'){
                            def ejecucion = load 'gradle.groovy'
                            ejecucion.call()
                        } else {
                            def ejecucion = load 'maven.groovy'
                            ejecucion.call()
                        }

                    }
                }
            }
        }

        post {

            success {
                slackSend color: 'good', message: "[Pablo Rocco][${env.JOB_NAME}][${env.TOOL}] Ejecución Exitosa", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

            failure {
                slackSend color: 'danger', message: "[Pablo Rocco][${env.JOB_NAME}][${env.TOOL}] Ejecución fallida en [${env.STAGE}]", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

        }
    }  

}

return this;