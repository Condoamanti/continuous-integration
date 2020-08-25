package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName = "Dockerfile", String line, String encoding = "UTF-8") {
        def test = script.fileExists("${fileName}")
        println(test)
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