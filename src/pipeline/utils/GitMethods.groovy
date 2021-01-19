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

	println "Creando rama ${newBranch} a partir de ${origin}"

	sh 'git config --global url."git@github.com:".insteadOf "https://github.com/"'

    def output =  sh (script :"git reset --hard HEAD" , returnStdout: true)
    output =  sh (script :"git pull" , returnStdout: true)
    output =  sh (script :"git checkout ${origin}" , returnStdout: true)
    output =  sh (script :"git checkout -b ${newBranch}" , returnStdout: true)
    output =  sh (script :"git push origin ${newBranch}" , returnStdout: true)

	/*
	println "DEBUG: origin=" + origin
	println "DEBUG: newBranch=" + newBranch 
 
	sh 'git pull'
	sh 'git checkout ' + origin
	sh 'git checkout -b ' + newBranch
	// sh 'git push origin ' + newBranch
	sh 'git push https://jibanez123456:jibanez74@github.com/G4-Diplomado-DevOps/ms-iclab.git'
	
	sh 'git checkout ' + origin
	*/
}