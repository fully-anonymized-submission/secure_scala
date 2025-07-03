def findFile(filename: String) = {
    val command = s"find . -name $filename"
    val result = sys.process.Process(command).!!
    result
}