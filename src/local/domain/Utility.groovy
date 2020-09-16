package local.domain
class Utility {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utility(script) {
        this.script = script
    }

    def getProjectInfo(projectVariables) {
        def projectInfo = [:]
        try {
            projectInfo.commitHash = (projectVariables.GIT_COMMIT).substring(0,8)
            projectInfo.gitUrl = (projectVariables.GIT_URL)
            return projectInfo
        } catch (e) {
            echo "Exception: ${e}"
            currentBuild.result = 'FAILURE'
        } finally {
        } // finally end
    }
}