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

    def login(String username, String password, String url) {
        script.sh "docker login -u ${username} -p ${password} ${url}"
    }
    def logout(url) {
        script.sh "docker logout ${url}"
    }

    def build(String imageName, String imageRepository = "private_repository", String imageTag, String path = ".") {
        script.sh "docker build -t ${imageName}/${imageRepository}:${imageTag} ${path}"
    }

    def push (String imageName, String imageRepository = "private_repository", String imageTag = "latest") {
        script.sh "docker push ${imageName}/${imageRepository}:${imageTag}"
    }

    
}