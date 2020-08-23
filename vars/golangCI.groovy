import local.domain.*
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Ensure imported classes are null
    Docker docker = null

    try {
        node("jenkins-slave") {
            stage("Clean Workspace") {
                cleanWs()
            }

            stage("Create Dependencies") {
                docker = new Docker(this)
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