package local.domain
class Go {
    def script

    // Constructor with the instance of the script invoking this specific class
    Go(script) {
        this.script = script
    }

    def get(String projectPath) {
        script.sh "go get ${projectPath}"
    }

    def set(String projectPath) {
        script.sh "cd /go/src/${projectPath}"
    }
    def build(String projectPath) {
        script.sh "go build -o /go/src/${projectPath} ${projectPath}"
    }

    def run(String projectPath = ".") {
        script.sh "go run /go/src/${projectPath}"
    }
}