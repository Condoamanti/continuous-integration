package local.domain
class Docker {
    def script

    // Constructor with the instance of the script invoking this specific class
    Docker(script) {
        this.script = script
    }

    def login(String username, String password, String url) {
        script.sh "docker login -u ${username} -p ${password} ${url}"
    }
    def logout() {
        script.sh "docker logout"
    }

    def build(String imageName, String imageTag, String path = ".") {
        script.sh "docker build -t ${imageName}:${imageTag} ${path}"
    }

    def push (String imageName, String imageTag = "latest") {
        script.sh "docker push ${imageName}:${imageTag}"
    }

    def remove (String imageName, String imageTag = "latest") {
        script.sh "docker image rm ${imageName}:${imageTag}"
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
}