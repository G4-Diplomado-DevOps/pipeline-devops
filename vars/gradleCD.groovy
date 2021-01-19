import pipeline.utils.Validator
import pipeline.utils.GitMethods

//def call(stage_param, branch_name){
// def call(String choosenStages, String pipelineStages){
def call(){
        def validador = new Validator()

        def stages = ['gitDiff','nexusDownload','runJar','test','gitMergeMaster','gitDevelop','gitTagMaster']

        def so = isUnix() ? 'Linux' : 'Windows'
        figlet so
 
        stages.each{
                if (validador.isValidStage(it, params.stage)){       
                        stage(it){
                                try{
                                        "$it"()
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
                
        env.RELV = sh ( script: " echo ${GIT_BRANCH}|sed -n 's/.*\\(release-v.-.-.*\\).*/\\1/p' ", returnStdout:true )
//        if (env.GIT_BRANCH.contains('*release*')){
        if ( env.RELV.contains('release-v1-0-0')){
            def git = new GitMethods()

            if (git.checkIfBranchExists('master')){
                echo "Rama existe"
                git.diffBranch('master','release-v1-0-0')
            } else {
                                echo "no existe master, verificar branch"
            }} else {
            echo "la rama ${env.GIT_BRANCH} no corresponde como rama release, no se puede hacer delivery ${env.RELV}"
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
        sh 'nohup java -jar DevOpsUsach2020-0.0.1.jar &'
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
                git.gitMerge('release-v1-0-0','master')
        }

def gitMergeDevelop() {
                script {
                        env.ETAPA = 'GitMergeDevelop'
                        figlet env.ETAPA
                }
                def git = new GitMethods()
                git.gitMerge('release-v1-0-0','develop')

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
