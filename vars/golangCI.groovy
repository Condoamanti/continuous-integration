import local.domain.*

podTemplate(
  cloud: 'kubernetes',
  namespace: 'jenkins',
  activeDeadlineSeconds: '300',
  showRawYaml: 'false',
  containers: [
  containerTemplate(
    name: 'centos',
    image: 'centos:centos7',
    ttyEnabled: true,
    privileged: true,
    command: '/usr/sbin/init'
  )
]) {


    try {
        node(POD_LABEL) {
        def call(body) {
            def config = [:]
            body.resolveStrategy = Closure.DELEGATE_FIRST
            body.delegate = config
            body()

            // Ensure imported classes are null
            Docker docker = null

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
    }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}