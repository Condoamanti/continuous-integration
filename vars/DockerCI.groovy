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
            environment {
                 test_secret = credentials("dockerhub_password")
            }      

            stages {
                stage("Clean Workspace") {
                    cleanWs()
                    echo "secret: ${test_secret}"
                    echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                }

                stage ("Create Class Dependencies") {
                    docker = new Docker(this)
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
                        utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} update --assumeyes --quiet")
                    }
                    
                    // Add line to upgrade
                    if (config.upgrade) {      
                        utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} upgrade --assumeyes --quiet")
                    }

                    // Add lines to install dependency packages
                    for (i in config.packages) {
                        utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} install --assumeyes --quiet ${i}")
                    }

                    // Add line to clean package cache
                    utilities.appendFile("${config.fileName}", "RUN ${osPackageManager} clean all")
                }

                stage("Create Docker Image") {
                    docker.build("${config.imageDestinationName}", "${config.imageDestinationTag}")
                }
            } // stages end
        } // node end
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    }
}