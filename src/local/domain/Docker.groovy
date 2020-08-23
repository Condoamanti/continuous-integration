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
}