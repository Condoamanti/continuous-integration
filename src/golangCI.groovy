import local.domain.*
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Docker = null

    try {
        node("jenkins-slave") {
            stage("stage1") {
                docker = new Docker(this)
            }

            stage("stage2") {
                docker.test("test-message")
            }
        }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}