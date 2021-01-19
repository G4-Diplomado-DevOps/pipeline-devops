import pipeline.utils.Validator
import pipeline.utils.GitMethods

//def call(stage_param, branch_name){
// def call(String choosenStages, String pipelineStages){
def call(){
    def validador = new Validator()
        
    def stages = ['gitDiff','nexusDownload','runJar','test','gitMergeMaster','gitMergeDevelop','gitTagMaster']

    def so = isUnix() ? 'Linux' : 'Windows'
    figlet so
 
    stages.each{
        if (validador.isValidStage(it, params.stage)){       
            stage(it){
                try {
                    "$it"()
                } catch (Exception e) {
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
    sh 'git diff origin/main...' + env.GIT_BRANCH
}


def nexusDownload() {
    script {
        env.ETAPA = 'NexusDownload'
        figlet env.ETAPA
    }
    if (isUnix()) {
        sh "curl -X GET -u admin:devops4 http://34.229.88.5:8085/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
    } else {
        bat "curl -X GET -u admin:devops4 http://34.229.88.5:8085/repository/laboratorio-grupo-4/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
    }
}

def runJar() {
    script {
        env.ETAPA = 'Run'
        figlet env.ETAPA
    }
    if (isUnix()) {
        sh 'nohup java -jar DevOpsUsach2020-0.0.1.jar &'
    } else {
        bat 'start /B mvn spring-boot:run'
        sleep 60
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
    if (isUnix()) {
        sh 'git tag -l "v1-0-0"'
    } else {
        bat 'git tag -l "v1-0-0"'
    }
}

return this;