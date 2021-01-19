package pipeline.utils.git

def chekIfBranchExists(String branch){
	def output = sh ( script: "git ls-remote --heads origin ${branch}", returnStdout:true ) 

	if(output?.trim()){
		return true
	} else {
		return false
	}
}


def deleteBranch(String branch){
	sh "git push origin --delete ${branch}"
}

def createBranch(String origin, String newBranch){
	sh ''' 
		git fetch -p
		git checkout """${origin}""" ; git pull
		git checkout -b """${newBranch}"""
		git push origin """${newBranch}"""
		git checkout """${origin}""" ; git pull
		git branch -d """${newBranch}"""
	'''
}

def diffBranch(String origin, String master){
	println "origen: ${origin} master : ${master}"
	sh '''
		git checkout """${origin}""" ; git pull
                git fetch -p
                git checkout """${master}""" ; git pull
                git fetch -p
                git diff """${origin}""" """{master}""" | xargs -I{.} echo {.}
        '''
}

def diffMerge(String origin, String master){
        sh '''
                git checkout """${origin}""" ; git pull
                git fetch -p
                git checkout """${master}""" ; git pull
                git fetch -p
                git merge """${origin}""" """master"""
        '''
}

def gitMerge(String origin, String master){
        sh '''
                git checkout """${origin}""" ; git pull
                git fetch -p
                git checkout """${master}""" ; git pull
                git fetch -p
                git merge """${origin}"""
		git add .
		git commit -m "se realiza merge a Master"
		git push origin """${master}"""
		git branch -d """${origin}"""
        '''
}

def gitTagMaster(){
        sh 'git checkout ${master} ; git pull;git fetch -p'
        def versionTag = sh ( script: " git reflog |sed -n 's/.*\\(release-v.-.-.*\\).*/\\1/p' ", returnStdout:true )
        sh 'git tag ${versionTag} -m "Primera versión"'
	sh 'git push --tags'
}


return this;
