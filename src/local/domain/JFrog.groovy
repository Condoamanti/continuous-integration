package local.domain
class JFrog {
    def script

    // Constructor with the instance of the script invoking this specific class
    JFrog(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName = "Dockerfile", String line, String encoding = "UTF-8") {
        if (script.fileExists("${fileName}") == false) {
            // Create file if it does not exist already
            script.writeFile(file: "${fileName}", text: "${line}")
        } else {
            // Append text to fileName
            def currentText = script.readFile "${fileName}"
            script.writeFile file: "${fileName}", text: "${currentText}\n${line}"
        }
    }

    def publish(String repositoryName, String packageVersion, String repositoryUrl, String repositoryUser, String repositoryPassword) {
        script.sh "jfrog rt go-publish ${repositoryName} ${packageVersion} --url=${repositoryUrl} --user=${repositoryUser} --password=${repositoryPassword}"
    }

    def publish2(String repositoryName, String packageVersion, String repositoryUrl, String repositoryUser, String repositoryPassword) {
        script.jfrog "rt go-publish ${repositoryName} ${packageVersion} --url=${repositoryUrl} --user=${repositoryUser} --password=${repositoryPassword}"
    }
}