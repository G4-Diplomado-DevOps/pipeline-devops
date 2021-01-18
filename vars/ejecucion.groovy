
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

    pipeline {
        agent any

        parameters {
            string (
                name: 'stage',
                defaultValue: '',
                description: 'Selecciona stage a ejecutar'
            )
            string (
                name: 'releaseVersion',
                defaultValue: '',
                description: 'Ingresar version formato: {major}-{minor}-{patch}, ejemplo: 0-0-0'
            )
        }
        
        stages {
            stage('Pipeline') {
                steps {
                    script{

                        figlet params.TOOL

                        env.RELEASE_VERSION = params.releaseVersion
                        
                        if(env.GIT_BRANCH=='develop' || env.GIT_BRANCH.contains('feature'))
                        {
                            gradleCI.call();
                        } else if(env.GIT_BRANCH.contains('release')) {
                            gradleCD.call();
                        } else {
                            // Do Nothing.
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
