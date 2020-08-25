package local.domain
class Utilities {
    def script

    // Constructor with the instance of the script invoking this specific class
    Utilities(script) {
        this.script = script
    }

    // Method appendFile which appends text to an existing file
    def appendFile(File fileName, String line, String encoding = "UTF-8") {
        #def file = new File("${fileName}")

        if (file.exists() == false) {
            writeFile(file: "${fileName}", text: "${line}")
        } else {
            file.append("\n${line}")
        }
    }

}