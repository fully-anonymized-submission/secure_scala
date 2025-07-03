import scala.util.Try
import scala.sys.process._

// Function to run the shell command
def shell(command: String): String = {
  Try(command.!!) match {
    case Success(result) => result.trim
    case Failure(e) => e.getMessage
  }
}

// Function to search for a file using find
def searchFile(filename: String): String = {
  shell(s"find . -name $filename")
}

// Example usage
val fileToSearch = "input_file.txt"
val searchResult = searchFile(fileToSearch)
println(searchResult)