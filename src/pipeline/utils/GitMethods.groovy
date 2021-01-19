// Classpath
package pipeline.utils

def checkIfBranchExists(String branch) {

	def output = sh (script : "git pull ; git ls-remote --heads origin ${branch}", returnStdout: true)
    def respuesta = (output?.trim().contains("refs/heads/${branch}")) ? true : false
    return respuesta

	/*
  def var_outout = sh (script: "git pull; git ls-remote --heads origin ${branch}", returnStdout: true)

  if (var_outout?.trim()) {
  	return true
  }
  else {
  	return false
  }
  */

}

def deleteBranch(String branch) {
	println "Eliminando branch"
	sh "git pull; git push origin --delete ${branch}"
	sh "git branch -D ${branch}"
}


def createBranch(String origin, String newBranch) {
	println "DEBUG: origin=" + origin
	println "DEBUG: newBranch=" + newBranch 
 
	sh 'git pull'
	sh 'git checkout ' + origin
	sh 'git checkout -b ' + newBranch
	// sh 'git push origin ' + newBranch
	sh 'git push https://jibanez123456:jibanez74@github.com/G4-Diplomado-DevOps/ms-iclab.git'
	
	sh 'git checkout ' + origin
}