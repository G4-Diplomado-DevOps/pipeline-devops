
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

    pipeline {
        agent any

        parameters {
            choice (
                name: 'TOOL',
                choices:
                    [
                        'gradle',
                        'maven'
                    ],
                description: 'Selecciona herramienta'
            ) 
            string (
                name: 'stage',
                defaultValue: '',
                description: 'Selecciona stage a ejecutar'
            )
        }
        
        stages {
            stage('Pipeline') {
                steps {
                    script{
                        figlet params.TOOL

                        if (params.TOOL == 'gradle'){
                            gradle.call()
                        } else {
                            maven.call()
                        }

                    }
                }
            }
        }

        post {

            success {
                slackSend color: 'good', message: "[Grupo 4][${env.JOB_NAME}][${env.TOOL}] Ejecución Exitosa", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

            failure {
                slackSend color: 'danger', message: "[Grupo 4][${env.JOB_NAME}][${env.TOOL}] Ejecución fallida en [${env.STAGE}]", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

        }
    }  

}

return this;
