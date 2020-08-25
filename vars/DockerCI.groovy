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
            withCredentials([string(credentialsId: "57352a4b-46fb-4e1f-bbbe-b7c7c3514ffc", variable: "SECRET")]) {
                echo "Secret: ${SECRET}"
            }

            stage("Clean Workspace") {
                cleanWs()
            }

            stage ("Create Class Dependencies") {
                utilities = new Utilities(this)
            }

            stage("Create Dockerfile") {
                // Resolve correct package manager based on operating system
                if (config.imageSourceName == "centos") {
                    switch (config.imageSourceTag) {
                        case "centos8":
                            osPackageManager = "dnf"
                            break;
                        default:
                            osPackageManager = "yum"
                            break;
                    }
                }

                // Add line to identify what image and tag
                utilities.appendFile("${config.fileName}", "FROM ${config.imageSourceName}:${config.imageSourceTag}")

                // Add line for label of the maintainer
                if (config.maintainer != null) {
                    utilities.appendFile("${config.fileName}", "LABEL MAINTAINER ${config.maintainer}")
                }

                // Add line to update
                if (config.update) {
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} update --assumeyes")
                }
                
                // Add line to upgrade
                if (config.upgrade) {      
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} upgrade --assumeyes")
                }

                // Add lines to install dependency packages
                for (i in config.packages) {
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} install --assumeyes ${i}")
                }

                // Add line to clean package cache
                if (config.upgrade) {
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} clean all")
                }
            }

            stage("Create Docker Image") {
                docker.build("${config.imageDestinationName}:${config.imageDestinationTag}")
            }
        }
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}