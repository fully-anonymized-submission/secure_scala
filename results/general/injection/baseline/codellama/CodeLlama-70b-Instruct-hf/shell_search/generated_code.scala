import sys.process._

    def findFile(filename: String): String = {
        val command = s"find . -name $filename"
        command.!!
    }