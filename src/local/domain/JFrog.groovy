package local.domain
class JFrog {
    def script

    // Constructor with the instance of the script invoking this specific class
    JFrog(script) {
        this.script = script
    }

    def publish(String packageRepositoryName, String packageVersion, String packageRepositoryUrl, String packageRepositoryUsername, String packageRepositoryPassword, String projectFileExlusions) {
        script.sh "jfrog rt go-publish ${packageRepositoryName} ${packageVersion} --url=${packageRepositoryUrl} --user=${packageRepositoryUsername} --password=${packageRepositoryPassword} --exclusions=Jenkinsfile;.gitignore;${projectFileExlusions}
    }
}