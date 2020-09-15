import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

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
            command: '/bin/sh'
        )
    ]) {

        try {
            node(POD_LABEL) {
                //stage("Get Golang Project") {
                //    git url: 'https://github.com/Condoamanti/programming.git'
                //} // stage end

                container("alpine-golang") {
                    echo "${config.message}"
                    go get "github.com/Condoamanti/programming"
                } // container end
            } // node end
        } catch (e) {
            echo "Exception: ${e}"
            currentBuild.result = 'FAILURE'
        } finally {
        } // finally end
    } // podTemplate end
} // call(body) end