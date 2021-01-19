// Classpath
package pipeline.utils

def checkIfBranchExists(String branch) {

	def output = sh (script : "git pull ; git ls-remote --heads origin ${branch}", returnStdout: true)
    def response = (output?.trim().contains("refs/heads/${branch}")) ? true : false
    return response
}

def deleteBranch(String branch) {
	sh "git pull ; git push origin --delete ${branch}; git branch -D ${branch}"
}

def createBranch(String origin, String newBranch) {

    def output =  sh (script :"git reset --hard HEAD" , returnStdout: true)
    output =  sh (script :"git pull" , returnStdout: true)
    output =  sh (script :"git checkout ${origin}" , returnStdout: true)
    output =  sh (script :"git checkout -b ${newBranch}" , returnStdout: true)
    output =  sh (script :"git push origin ${newBranch}" , returnStdout: true)
}