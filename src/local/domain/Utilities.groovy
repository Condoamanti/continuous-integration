package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line) {
        def file = new File("${fileName}")
        file.createNewFile()
    }
}