import local.domain.*

def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def String osPackageManager = null
    def String osPackageManagerParameters = null
    def String credentialsId = null
    
    // Ensure imported classes are null
    Docker docker = null
    try {
        node("jenkins-slave") {  
            stage("Clean Workspace") {
                cleanWs()
            }

            stage ("Create Class Dependencies") {
                docker = new Docker(this)
            }

            stage("Create Dockerfile") {
                // Resolve correct package manager based on operating system
                switch (config.imageSourceName) {
                    case ["centos"]:
                        osPackageManagerParameters = "--assumeyes --quiet"
                        switch (config.imageSourceTag) {
                            case ["centos8"]:
                                osPackageManager = "dnf"
                                break;
                            default:
                                osPackageManager = "yum"
                                break;
                        }
                    case ["alpine"]:
                        osPackageManagerParameters = "--quiet --no-progress --no-cache"
                        switch (config.imageSourceTag) {
                            default:
                                osPackageManager = "apk"
                                break;
                        }
                }

                // Add line to identify what image and tag
                docker.appendFile("${config.fileName}", "FROM ${config.imageSourceName}:${config.imageSourceTag}")

                // Add line for label of the maintainer
                if (config.maintainer != null) {
                    docker.appendFile("${config.fileName}", "LABEL MAINTAINER ${config.maintainer}")
                }

                // Package installation lines
                switch (osPackageManager) {
                    case ["yum", "dnf"]:
                        // Add line to update
                        if (config.update) {
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} update ${osPackageManagerParameters}")
                        }
                        // Add line to upgrade
                        if (config.upgrade) {      
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} upgrade ${osPackageManagerParameters}")
                        }
                        // Add lines to install dependency packages
                        for (i in config.packages) {
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} install ${osPackageManagerParameters} ${i}")
                        }
                        // Add line to clean package cache
                        docker.appendFile("${config.fileName}", "RUN ${osPackageManager} clean all")
                        break;
                    case ["apk"]:
                        // Add line to update
                        if (config.update) {
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} update ${osPackageManagerParameters}")
                        }
                       // Add line to upgrade
                        if (config.upgrade) {      
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} upgrade ${osPackageManagerParameters} ")
                        }
                        // Add lines to install dependency packages
                        for (i in config.packages) {
                            docker.appendFile("${config.fileName}", "RUN ${osPackageManager} add ${osPackageManagerParameters} ${i}")
                        }
                        break;
                }

                // Add lines to configure environment variables
                for (i in config.environmentVariables) {
                    docker.appendFile("${config.fileName}", "ENV ${i}")
                }

                // Add lines to run additional commands
                for (i in config.additionalCommands) {
                    docker.appendFile("${config.fileName}", "RUN ${i}")
                }
                
            }

            stage("Create Docker Image") {
                docker.build("${config.imageDestinationRepositoryUrl}/${config.imageDestinationName}", "${config.imageDestinationTag}")
            }
            
            stage ("Push Docker Image") {
                switch (config.imageDestinationRepositoryUrl) {
                    case "docker.io":
                        // Use dockerhub credentials
                        credentialsId = "dockerhub_credentials"
                        break;
                    default:
                        // Use artifactory credentials
                        credentialsId = "artifactory_credentials"
                        break;
                } // switch end
                println("credentialsId: ${credentialsId}")
                withCredentials([usernamePassword(credentialsId: "${credentialsId}", usernameVariable: 'dockerRepositoryUsername', passwordVariable: 'dockerRepositoryPassword')]) {
                    docker.login("${dockerRepositoryUsername}", "${dockerRepositoryPassword}", "${config.imageDestinationRepositoryUrl}")
                    docker.push("${config.imageDestinationRepositoryUrl}/${config.imageDestinationName}", "${config.imageDestinationTag}")
                    docker.logout("${config.imageDestinationRepositoryUrl}")
                }
            }

            stage ("Clean Docker Images") {
                docker.remove("${config.imageDestinationRepositoryUrl}/${config.imageDestinationName}", "${config.imageDestinationTag}")
                docker.remove("${config.imageSourceName}", "${config.imageSourceTag}")
            }
        
        } // node end
    } catch (e) {
        echo "Exception: ${e}"
        currentBuild.result = 'FAILURE'
    } finally {
    } // finally end
}