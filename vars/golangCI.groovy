import local.domain.*
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Docker docker = null

    try {
        node("jenkins-slave") {
            stage("stage1") {
                docker = new Docker(this)
            }

            stage("stage2") {
                docker.test()
            }
        }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}