import pipeline.utils.Validator
import pipeline.utils.GitMethods

def call(){


    // Variable para definir si los steps anteriores han sido correctos
    // Comienza en false permitiendo que se valide inicie de step en build & test
    def success = false

    // Importacion de funciones desde Util
    def util = new Validator()

    repo_name = env.GIT_URL.split("/").last().replaceAll('.git', '')

    println "DEBUG: env.GIT_URL="+env.GIT_URL

    tecnologhy = util.technologyType(repo_name)

    figlet tecnologhy

    mavenValid = util.validateTool()

    if (mavenValid) {
        figlet "Tool Maven Validada"
    }
    else {
        figlet "Tool Maven NO Validada"
    }

    if (util.isValidReleaseVersion(env.RELEASE_VERSION)) {
            figlet "version validada=" + env.RELEASE_VERSION
    }
    else {
        error "ERROR: version invalida" + env.RELEASE_VERSION
    }

    

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
                    sh './mvnw clean compile -e'   
                } else {
                    bat "./mvnw.cmd clean compile -e"
                }

                // Si pasa el try el step fue exitoso
                success = true

            } catch(Exception e) {
                echo "Error en stage "+STAGE_NAME
            }

        }
        println "DEBUG: compile-> success:" + success
    }

    if(util.validateStage('unitTest') && success)
    {
        stage('unitTest') {

            env.STAGE = STAGE_NAME

            try {
                if (isUnix()) {
                    sh './mvnw clean test -e'   
                } else {
                    bat './mvnw.cmd clean test -e'
                }

                // Si pasa el try el step fue exitoso
                success = true

            } catch(Exception e) {
                echo "Error en stage "+STAGE_NAME
            }
        }
        println "DEBUG: unitTest-> success:" + success
    }

    if(util.validateStage('jar') && success)
    {
        stage('jar') {

            env.STAGE = STAGE_NAME

            try {
                if (isUnix()) {
                    sh './mvnw clean package -e'   
                } else {
                    bat './mvnw.cmd clean package -e'
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
                def scannerHome = tool 'sonar_scanner';

                // Nombre extraido desde Jenkins > Configurar el sistema > SonarQube servers
                withSonarQubeEnv('sonar_server') {

                    if (isUnix()) {
                        sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ms-iclab -Dsonar.java.binaries=build"    

                    } else {
                        bat "${scannerHome}\\bin\\sonar-scanner -Dsonar.projectKey=ms-iclab -Dsonar.java.binaries=build"
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
                                filePath: './build/DevOpsUsach2020-0.0.1.jar'
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
                error "Error en stage "+STAGE_NAME
            }

        }
    }

}

return this;
