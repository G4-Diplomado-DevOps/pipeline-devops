
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

    stage('Compile Code') {
        env.STAGE = STAGE_NAME
        sh './mvnw clean compile -e'   

    }

    stage('Test Code') {
        env.STAGE = STAGE_NAME
        sh './mvnw clean test -e'   

    }

    stage('Jar Code') {
        env.STAGE = STAGE_NAME
        sh './mvnw clean package -e'   

    }

    stage('SonarQube') {
        env.STAGE = STAGE_NAME
        withSonarQubeEnv(installationName: 'sonar_server') {
            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
        }

    }
    
    stage('Upload Nexus') {
        env.STAGE = STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'taller-10-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/Users/procco/personal/usach/Modulo3/repositorios/ejemplo-maven/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]] 

    }

}

return this;