package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line, String encoding = "UTF-8") {
        if (new File("${fileName}").exists() == false) {
            script.println("HERE1")
            script.writeFile(file: "${fileName}", text: "${line}")
        } else {
            script.println("HERE2")
            new File("${fileName}").append("\n${line}")
        }
    }

}