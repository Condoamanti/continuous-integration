package local.domain
class JFrog {
    def script

    // Constructor with the instance of the script invoking this specific class
    JFrog(script) {
        this.script = script
    }

<<<<<<< HEAD
    def publish(String repositoryName, String packageVersion, String repositoryUrl, String repositoryUser, String repositoryPassword) {
        script.sh "jfrog rt go-publish ${repositoryName} ${packageVersion} --url=${repositoryUrl} --user=${repositoryUser} --password=${repositoryPassword}"
=======
    def publish(String packageRepositoryName, String packageVersion, String packageRepositoryUrl, String packageRepositoryUsername, String packageRepositoryPassword) {
        script.sh "jfrog rt go-publish ${packageRepositoryName} ${packageVersion} --url=${packageRepositoryUrl} --user=${packageRepositoryUsername} --password=${packageRepositoryPassword}"
>>>>>>> 3f49ef5f6290bf9956cc1898b6c2fb0d2dc4b305
    }
}