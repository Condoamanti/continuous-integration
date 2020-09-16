import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    // Ensure imported classes are null
    Go go = null

    // podTemplate configuration for the container running the below stages
    podTemplate(
    cloud: 'kubernetes',
    namespace: 'jenkins',
    activeDeadlineSeconds: '300',
    showRawYaml: 'false',
    runAsUser: "0",
    runAsGroup: "1000",
    imagePullSecrets: [ "artifactory-credentials"],
    containers: [
        containerTemplate(
            name: 'alpine-golang',
            image: 'artifactory.jittersolutions.com/docker-local/alpine-golang:latest',
            ttyEnabled: true,
            privileged: true,
            command: '/bin/sh',
            ports: [portMapping(name: 'http', containerPort: 8081, hostPort: 8081)],
        )
    ]) {

        try {
            node(POD_LABEL) {
                container("alpine-golang") {
                    stage ("Create Class Dependencies") {
                        go = new Go(this)
                    }

                    stage ("Get Go Project Files") {
                        go.get("${config.projectPath}")
                    } //stage end
                    
                    stage ("Build Go Project") {
                        //go.build("${config.projectPath}")
                        sh "cd \$GOPATH/src/${config.projectPath}"
                    } //stage end

                    stage ("Run Go Project") {
                        go.run()
                    } //stage end
                } // container end
            } // node end
        } catch (e) {
            echo "Exception: ${e}"
            currentBuild.result = 'FAILURE'
        } finally {
        } // finally end
    } // podTemplate end
} // call(body) end