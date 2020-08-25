import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def osPackageManager = null
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
                // Resolve correct package manager based on operating system
                if (config.imageName == "centos") {
                    switch (config.imageTag) {
                        case "centos8":
                            osPackageManager = "dnf"
                            break;
                        default:
                            osPackageManager = "yum"
                            break;
                    }
                }

                utilities.appendFile("${config.fileName}", "FROM ${config.imageName}:${config.imageTag}")

                if (config.maintainer != null) {
                    utilities.appendFile("${config.fileName}", "LABEL MAINTAINER ${config.maintainer}")
                }

                // Add line to update docker image
                if (config.update) {
                    
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} update --assumeyes")
                }
                
                // Add line to upgrade docker image
                if (config.upgrade) {
                    
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} upgrade --assumeyes")
                }

                print("${config.packages}")
                for (i in config.packages) {
                    println(i)
                }

                sh """
                ls -lah
                cat ./Dockerfile
                """
            }
        }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}