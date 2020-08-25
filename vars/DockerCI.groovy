import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Ensure imported classes are null
    Docker docker = null
    Utilities utilities = null

    try {
        node("jenkins-slave") {
            stage("Clean Workspace") {
                cleanWs()
            }

            stage ("Create Class Dependencies") {
                utilities = new Utilities(this)
            }

            stage("Create Dockerfile") {
                utilities.appendFile("Dockerfile", "FROM ${config.imageName}:${config.imageTag}")
                sh """
                cat ./Dockerfile
                """
            }

            stage("Run Docker Class") {
                docker.test("${config.message}")
            }
        }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}