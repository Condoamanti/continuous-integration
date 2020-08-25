package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line, String encoding = "UTF-8") {
        
        if ((script.println new File('input.txt').exists()) == false) {
            script.println("HERE1")
            script.appendFile(file: "${fileName}", text: "${line}")
        } else {
            script.println("HERE2")
            script.appendFile(file: "${fileName}", text: "\n${line}")
        }
    }

}