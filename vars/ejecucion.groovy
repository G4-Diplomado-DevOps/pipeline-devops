
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
                        'maven',
                        'gradle'
                    ],
                description: 'Selecciona herramienta de compilacion'
            )
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
                            if (params.TOOL == 'gradle'){
				figlet "Integración Continua"
                                gradleCI.call();
                            } else {
				figlet "Integración Continua"
                                mavenCI.call();
                            }

                        } else if(env.GIT_BRANCH.contains('release')) {
                            
                            if (params.TOOL == 'gradle'){
				figlet "Entrega Continua"
                                gradleCD.call();
                            } else {
				figlet "Entrega Continua"
                                mavenCD.call();
                            }
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
