package local.domain
class Golang {
    def script

    // Constructor with the instance of the script invoking this specific class
    Golang(script) {
        this.script = script
    }

    def get(String projectPath) {
        script.sh "go get ${projectPath}"
    }

    def build(String projectPath) {
        script.sh "go build -o \$GOPATH/src/${projectPath} ${projectPath}"
    }

    def run(String projectPath = ".") {
        script.sh "go run ${projectPath}"
    }
}