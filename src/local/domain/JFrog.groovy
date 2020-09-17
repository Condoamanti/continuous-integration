package local.domain
class JFrog {
    def script

    // Constructor with the instance of the script invoking this specific class
    JFrog(script) {
        this.script = script
    }

    def publish(String repositoryName, String packageVersion, String repositoryUrl, String repositoryUser, String repositoryPassword) {
        script.sh "jfrog rt go-publish ${repositoryName} ${packageVersion} --url=${repositoryUrl} --user=${repositoryUser} --password=${repositoryPassword}"
    }

    def publish2(String repositoryName, String packageVersion, String repositoryUrl, String repositoryUser, String repositoryPassword) {
        script.sh "jfrog rt go-publish ${repositoryName} ${packageVersion} --url=${repositoryUrl} --user=${repositoryUser} --password=${repositoryPassword}"
    }
}