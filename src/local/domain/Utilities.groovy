package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line) {
        def current = ""
        if (File(fileName).exists()) {
            current = readFile fileName
        } else {
            writeFile file: fileName, text: line
        }
        writeFile file: fileName, text: current + "\n" + line
    }
}