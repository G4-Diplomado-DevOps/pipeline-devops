import pipeline.utils.*

//def call(stage_param, branch_name){
 def call(String choosenStages){
	
	def utils = new test.UtilMethods()
//Quizás leer un archivo con los stages en vez de tenerlos
	def pipeline = (utils.isCIorCD().contains(ci)) ? ['compile','unitTest','jar','sonar','nexusUpload','gitCreateRelease'] : ['gitDiff','nexusDownload','run','test','gitMergeMaster','gitDevelop','gitTagMaster']
//    def validator = new Validator()
	def stages = utils.getValidateStages(choosenStages, pipelineStages)


	flow_name = validator.getNameFlow(branch_name)

	figlet flow_name

	stages.each{
		stages(it){
			try{
				"${it}"()
			} catch( Exception e){
				error "Stage ${it} tiene problemas : ${e}" 
			}

		}
	}
 }

/*
def createRelease(){
	if (env.GIT_BRANCH.contains('develop')){

		del git = new git.GitMethods()

		if (git.chekIfBranchExists('release-v1-0-0')){
			echo "Rama existe"
			git.deleteBranch('release-v1-0-0')
			echo "se elimino rama"
			git.createBranch(env.GIT_BRANCH,'release-v1-0-0')
			echo "branch creado"
		}else{
			git.createBranch(env.GIT_BRANCH,'release-v1-0-0')
	}else{
		echo "la rama $(env.GIT_BRANCH) no corresponde como rama de origen para la creacion de un release"
	}
}
*/

//separamos los flujos CI/CD
//    switch(flow_name.toLowerCase()) {
//        case "integracion continua":
//            ciFlow(stage_param)
//        break;
//        case "despliegue continuo":
//            cdFlow(stage_param)
//        break;
//    }
//}

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
		sh "curl -X GET -u admin:devops4 http://34.229.88.5:8085/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
    }
    
	def run(){
		script {
			env.ETAPA = 'Run'
			figlet env.ETAPA
		}
       	sh 'nohup java -jar DevOpsUsach2020-0.0.1.jar &'            
	}
    
	def test() {
		script {
			env.ETAPA = 'Test'
			figlet env.ETAPA
		}
		sh 'sleep 20'
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'" 
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



return this;
