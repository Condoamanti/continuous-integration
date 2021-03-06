import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def String credentialsId = null
    def String packageRepositoryName = null
    def String packageRepositoryUrl = null

    // Ensure imported classes are null
    Go go = null
    JFrog jfrog = null

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
            alwaysPullImage: true,
            ttyEnabled: true,
            //privileged: true,
            command: '/bin/sh',
            ports: [portMapping(name: 'http', containerPort: 8081, hostPort: 8081)],
        )
    ]) {

        try {
            node(POD_LABEL) {
                container("alpine-golang") {
                    stage ("Create Class Dependencies") {
                        go = new Go(this)
                        jfrog = new JFrog(this)
                    } // stage end

                    stage ("Get Go Project Files") {
                        go.get("${config.projectPath}")
                    } // stage end
                    
                    stage ("Build Go Project") {
                        go.build("${config.projectPath}")
                        
                    } // stage end

                    stage ("Publish Go Project") {
                        switch (config.projectRepositoryDestination) {
                            default:
                                // Use artifactory credentials
                                credentialsId = "artifactory_credentials"
                                packageRepositoryName = "go-local"
                                packageRepositoryUrl = "https://artifactory.jittersolutions.com/artifactory"
                                break;
                        } // switch end
                        println("credentialsId: ${credentialsId}")
                        dir("/go/src/${projectPath}") {
                            sh "ls"
                            sh "pwd"
                            withCredentials([usernamePassword(credentialsId: "${credentialsId}", usernameVariable: 'packageRepositoryUsername', passwordVariable: 'packageRepositoryPassword')]) {
                                jfrog.publish("${packageRepositoryName}", "${BUILD_NUMBER}", "${packageRepositoryUrl}", "${packageRepositoryUsername}", "${packageRepositoryPassword}")
                            }
                        } // dir end
                    } // stage end
                } // container end
            } // node end
        } catch (e) {
            echo "Exception: ${e}"
            currentBuild.result = 'FAILURE'
        } finally {
        } // finally end
    } // podTemplate end
} // call(body) end