package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(File fileName, String line) {
        
        if (!fileName.exists()) {
            def file = new File("${fileName}")
            file.createNewFile()
            file.append("${line}\n")
        } else {
            def file = new File("${fileName}")
            file.append("${line}\n")
        }

    }
}