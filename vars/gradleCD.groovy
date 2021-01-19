import pipeline.utils.Validator
import pipeline.utils.GitMethods

//def call(stage_param, branch_name){
// def call(String choosenStages, String pipelineStages){
def call(){
        def validador = new Validator()
//Quizás leer un archivo con los stages en vez de tenerlos
//        def pipelineStages = ['gitDiff';'nexusDownload';'runJar';'test';'gitMergeMaster';'gitDevelop';'gitTagMaster']
        def stages = ['gitDiff','nexusDownload','runJar','test','gitMergeMaster','gitDevelop','gitTagMaster']

        //    def validator = new Validator()
//        def stages = validador.validateStage(choosenStages,pipelineStages )
        //def stages = pipelineStages
        
        // Despliegue de sistema operativo desde donde se corre pipeline (Para definir sh o bat)
        def so = isUnix() ? 'Linux' : 'Windows'
        figlet so
 
        stages.each{
                if (validador.isValidStage(it, params.stage)){       
                        stages(it){
                                try{
                                        echo " pasando por stage ${it}"
                                        //"{$it}"()
                                } catch( Exception e){
                                        error "Stage ${it} tiene problemas : ${e}"
                                }

                        }
                        
                }
        }
 }


def gitDiff(){
                script {
                        env.ETAPA = 'GitDiff'
                        figlet env.ETAPA
                }
        if (env.GIT_BRANCH.contains('*release*')){

            def git = new GitMethods()

            if (git.checkIfBranchExists('master')){
                echo "Rama existe"
                git.diffMerge('master','release-v1-0-0')
            } else {
                                echo "no existe master, verificar branch"
            }} else {
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

def runJar() {
                script {
                        env.ETAPA = 'Run'
                        figlet env.ETAPA
                }
        if (isUnix()) {
        sh 'nohup gradle boot Run &'
        } else {
        bat 'start gradle boot Run'
        }
}
def test() {
                script {
                        env.ETAPA = 'Test'
                        figlet env.ETAPA
                }
        if (isUnix()) {
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
                def git = new GitMethods()
                git.gitMerge('master','release-v1-0-0')
        }

def gitMergeDevelop() {
                script {
                        env.ETAPA = 'GitMergeDevelop'
                        figlet env.ETAPA
                }
                def git = new GitMethods()
                git.gitMerge('develop','release-v1-0-0')

        }

def gitTagMaster() {
                script {
                        env.ETAPA = 'GitTagMaster'
                        figlet env.ETAPA
                }
                def git = new GitMethods()
                git.gitTagMaster()
        }



return this;
