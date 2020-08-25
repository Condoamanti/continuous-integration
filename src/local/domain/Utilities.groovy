package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(String fileName, String line) {
        file = new File("${fileName}")

        if (!file.exists()) {
            file.createNewFile()
            file.append("\n${line}")
        } else {
            file.append("\n${line}")
        }

    }
}