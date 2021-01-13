import pipeline.utils.*
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(stage_param, branch_name){
    
    def validator = new Validator()

    flow_name = validator.getNameFlow(branch_name)

    figlet flow_name

    //separamos los flujos CI/CD

    switch(flow_name.toLowerCase()) {
        case "integracion continua":
            ciFlow(stage_param)
        break;
        case "despliegue continuo":
            cdFlow(stage_param)
        break;
    }

    
}

// Flujo CI
def ciFlow(stage_param){

    def validator = new Validator()

    if(validator.isValidStage('compile', stage_param)){
        stage('compile') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean compile -e'   

        }
    }

    if(validator.isValidStage('test', stage_param)){
        stage('test') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean test -e'   

        }
    }

    if(validator.isValidStage('jar', stage_param)){
        stage('jar') {
            env.STAGE = STAGE_NAME
            sh './mvnw clean package -e'   

        }
    }

    if(validator.isValidStage('sonar', stage_param)){
        stage('sonar'){
            env.STAGE = STAGE_NAME
            def scannerHome = tool 'sonar_scanner';
            withSonarQubeEnv('sonar_server') {
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ms-iclab -Dsonar.java.binaries=build"    
            }
        }
    }

    if(validator.isValidStage('nexusCI', stage_param)){ 
        stage('nexusCI') {
            env.STAGE = STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'laboratorio-grupo-4', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]] 

        }
    }

}

// Flujo CD
def cdFlow(stage_param){

    def validator = new Validator()

    if(validator.isValidStage('downloadNexus', stage_param)){
        stage('downloadNexus') {
            env.STAGE = STAGE_NAME
            sh "curl -X GET -u admin:devops4 http://34.229.88.5:8081/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"

        }
    }

    if(validator.isValidStage('runDownloadedNexus', stage_param)){
        stage('runDownloadedNexus') {
            env.STAGE = STAGE_NAME
            sh 'nohup java -jar DevOpsUsach2020-0.0.1.jar &'
            
        }
    }

    stage('rest') {
        env.STAGE = STAGE_NAME
        sleep 20
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'" 

    }

    stage('nexusCD') {
        env.STAGE = STAGE_NAME
        nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'laboratorio-grupo-4', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: './build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '1.0.0']]] 
    
    }

}

return this;
