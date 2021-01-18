import pipeline.utils.Validator
import pipeline.utils.GitMethods

//def call(stage_param, branch_name){
 def call(String choosenStages){
	
	def utils = new test.UtilMethods()
//Quizás leer un archivo con los stages en vez de tenerlos
	def pipeline = ['gitDiff','nexusDownload','run','test','gitMergeMaster','gitDevelop','gitTagMaster']

    def util = new Validator()
	def stages = utils.getValidateStages(choosenStages, pipelineStages)

    def so = isUnix() ? 'Linux' : 'Windows'
    figlet so

	flow_name = validator.getNameFlow(branch_name)

	figlet flow_name

	stages.each{
		stages(it){
			try{
				"${it}"
			}
			catch(){
				error "Stage ${it} tiene problemas : ${e}" 
			}

		}
	}


// Flujo CD
//        echo "Inicio CDmaven.goovy"
//	for (int i = 0 ; i < cstage.length; i++){
                        echo " ejecucion de for para ${cstage[i]}"
//	switch("${cstage[i]}"){
//	case: "gitDiff"

	def gitDiff(){
		script {
			env.ETAPA = 'GitDiff'
			figlet env.ETAPA
		}
        if (env.GIT_BRANCH.contains('*release*')){

            def git = new git.GitMethods()

            if (git.chekIfBranchExists('master')){
                echo "Rama existe"
                git.diffBranch('master',env.GIT_BRANCH)
            }else{
				echo "no existe master, verificar branch"
			}
        }else{
            echo "la rama ${env.GIT_BRANCH} no corresponde como rama release, no se puede hacer delivery"
        }
	}

	def nexusDownload() {
	    script {
			env.ETAPA = 'NexusDownload'
			figlet env.ETAPA
		}
		if (isUnix()){
			sh "curl -X GET -u admin:devops4 http://34.229.88.5:8085/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
		} else {
			bat "curl -X GET -u admin:devops4 http://34.229.88.5:8085/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
		}
    }
    
	def run(){
		script {
			env.ETAPA = 'Run'
			figlet env.ETAPA
		}
		if (isUnix()){
			sh 'nohup java -Dserver.port=8082 -jar DevOpsUsach2020-0.0.1.jar &'
		} else {
			bat 'start /B java -Dserver.port=8082 -jar DevOpsUsach2020-1.0.0.jar'
		} 
	}
    
	def test() {
		script {
			env.ETAPA = 'Test'
			figlet env.ETAPA
		}
		if (isUnix()){
			sh 'sleep 20'
			sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'" 	
		} else {
			bat 'sleep 20'
			bat "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'" 
		}
	}

	def gitMergeMaster() {
		script {
			env.ETAPA = 'GitMergeMaster'
			figlet env.ETAPA
		}
		def git = new git.GitMethods()
		git.DeployToMain(env.GIT_BRANCH,'release-v1-0-0')
	}

	def gitMergeDevelop() {
		script {
			env.ETAPA = 'GitMergeDevelop'
			figlet env.ETAPA
		}
		// TODO
	}

	def gitTagMaster() {
		script {
			env.ETAPA = 'GitTagMaster'
			figlet env.ETAPA
		}
		// TODO
	}

}

return this;
