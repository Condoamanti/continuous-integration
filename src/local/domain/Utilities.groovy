package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line, String encoding = "UTF-8") {
        def test = script.fileExists("${fileName}")
        println(test)
        if (script.fileExists("${fileName}") == false) {
            script.println("HERE1")
            script.writeFile(file: "${fileName}", text: "${line}")
        } else {
            script.println("HERE2")
            currentData = script.readFile "${fileName}"
            script.writeFile file: "${fileName}", text: "${currentData}\n${line}"
            //new File("${fileName}").append("\n${line}")
        }
    }

}