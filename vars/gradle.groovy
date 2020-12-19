
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
    
    stage('Build & test'){
        env.STAGE = STAGE_NAME
        sh "./gradlew clean build"
    }
    stage('Sonar'){
        env.STAGE = STAGE_NAME
        def scannerHome = tool 'sonar_scanner';
        withSonarQubeEnv('sonar_server') {
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"    
        }
    }
    stage('Run'){
        env.STAGE = STAGE_NAME
        sh "nohup bash gradlew bootRun &"
        sleep 20
    }
    stage('Rest'){
        env.STAGE = STAGE_NAME
        sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'" 
    }
    stage('Nexus'){
        env.STAGE = STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'taller-10-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    }
    
}

return this;