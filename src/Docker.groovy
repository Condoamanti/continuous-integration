package local.domain
class Docker {
    def script

    Docker(script) {
        this.script = script
    }

    def test(message = "Hello, World!") {
        print("${message}")
    }
}