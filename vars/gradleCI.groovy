import pipeline.utils.Validator
import pipeline.utils.GitMethods

def call(){

    // Variable para definir si los steps anteriores han sido correctos
    // Comienza en false permitiendo que se valide inicie de step en build & test
    def success = false

    // Importacion de funciones desde Util
    def util = new Validator()

    // Despliegue de sistema operativo desde donde se corre pipeline (Para definir sh o bat)
    def so = isUnix() ? 'Linux' : 'Windows'
    figlet so

    // En el primer step no se verifica el boolean success ya que es el primer step
    if(util.validateStage('compile'))
    {
        stage('compile') {

            env.STAGE = STAGE_NAME

            try {
                if (isUnix()) {
                    sh "gradle clean build"
                } else {
                    bat "gradle clean build"
                }

                // Si pasa el try el step fue exitoso
                sucess = true

            } catch(Exception e) {
                echo "Error en stage "+STAGE_NAME
            }

        }
    }

    if(util.validateStage('unitTest') && success)
    {
        stage('unitTest') {

            env.STAGE = STAGE_NAME

            try {
                if (isUnix()) {
                    sh "nohup bash gradlew bootRun &"
                } else {
                    bat 'start /B gradle bootRun'
                }

                // Si pasa el try el step fue exitoso
                success = true

            } catch(Exception e) {
                echo "Error en stage "+STAGE_NAME
            }
        }
    }

    if(util.validateStage('sonar') && success)
    {
        stage('sonar') {

            env.STAGE = STAGE_NAME

            try {
                // Nombre extraido desde Jenkins > Global tool configuration > SonarQube Scanner
                def scannerHome = tool 'sonar-scanner';

                // Nombre extraido desde Jenkins > Configurar el sistema > SonarQube servers
                withSonarQubeEnv('sonar_server') {

                    if (isUnix()) {
                        sh "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                    } else {
                        bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
                    }
                }

                // Si pasa el try el step fue exitoso
                success = true

            } catch(Exception e) {
                echo "Error en stage "+STAGE_NAME
            }
        }
    }

    if(util.validateStage('nexusUpload') && success)
    {
        stage('nexusUpload') {

            env.STAGE = STAGE_NAME

            try {
                nexusPublisher nexusInstanceId: 'nexus',
                    nexusRepositoryId: 'laboratorio-grupo-4',
                    packages: [
                        [
                            $class: 'MavenPackage',
                            mavenAssetList: [
                                [
                                    classifier: '',
                                    extension: 'jar',
                                    filePath: 'C:\\proyects\\diplomado\\gradle\\ejemplo-gradle\\build\\DevOpsUsach2020-0.0.1.jar'
                                ]
                            ],
                            mavenCoordinate: [
                                artifactId: 'DevOpsUsach2020',
                                groupId: 'com.devopsusach2020',
                                packaging: 'jar',
                                version: '0.0.1'
                            ]
                        ]
                    ]
                
                // Si pasa el try el step fue exitoso
                success = true

            } catch (Exception e) {
                echo "Error en stage "+STAGE_NAME
            }
        }
    }

    if(util.validateStage('gitCreateRelease') && success && env.GIT_BRANCH.contains('develop')) {
        stage('gitCreateRelease') {
            env.STAGE = STAGE_NAME

            try {
                def git = new GitMethods()

                // version = "1-1-2"

                if (git.checkIfBranchExists('release-v' + env.RELEASE_VERSION)) {
                    println "INFO: La rama existe"
                    git.deleteBranch('release-v' + env.RELEASE_VERSION) 
                    println "INFO: Rama eliminada"
                    git.createBranch(env.GIT_BRANCH, 'release-v' + env.RELEASE_VERSION)
                    println "INFO: Rama creada satisfactoriamente"
                } else {
                    git.createBranch(env.GIT_BRANCH, 'release-v' + env.RELEASE_VERSION)
                    println "INFO: Rama creada satisfactoriamente"
                }
            } catch (Exception e) {
                echo "Error en stage "+STAGE_NAME
            }

        }
    }

}

return this;