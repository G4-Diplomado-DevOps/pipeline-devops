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
	/*
	sh "git pull"
	sh "git checkout ${origin}"
	sh "git checkout -b ${newBranch}"
	sh "git push origin ${newBranch}"
	*/
	
	println "DEBUG: origin=" + origin
	println "DEBUG: newBranch=" + newBranch 
 
	sh 'git pull'
	sh 'git checkout ' + origin
	sh 'git checkout -b ' + newBranch
	// sh 'git push origin ' + newBranch
	sh 'git push https://jibanez123456:jibanez74@github.com/G4-Diplomado-DevOps/ms-iclab.git'
	
	sh 'git checkout ' + origin
	
}

def gitMerge(String branch_from, String branch_to) {
	env.GIT_COMMITTER_NAME = 'Guillermo Correa M.'
	env.GIT_COMMITTER_EMAIL = 'guillermocorreamartinez@gmail.com'
	sh "git checkout ${branch_to}"
    sh "git merge ${branch_from} --no-ff"
    sh "git push"
}
