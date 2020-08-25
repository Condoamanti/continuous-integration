package local.domain
class Docker {
    def script

    // Constructor with the instance of the script invoking this specific class
    Docker(script) {
        this.script = script
    }

    // Method test which prints out a message
    def test(message = "Hello, World!") {
        script.sh "echo \"message: ${message}\""
    }

    def login(url) {
        script.sh "docker login -u ${username} -p ${password} ${url}"
    }
    def logout(url) {
        script.sh "docker logout ${url}"
    }

    def build(String imageName, String imageTag, String path = ".") {
        script.sh "docker build -t ${imageName}:${imageTag} ${path}"
    }
}