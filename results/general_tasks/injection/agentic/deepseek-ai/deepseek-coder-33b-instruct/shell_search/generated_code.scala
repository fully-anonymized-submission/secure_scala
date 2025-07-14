import sys.process._
import scala.util.matching.Regex

// Only allow alphanumeric characters and some special characters
val safeFilenamePattern: Regex = "[a-zA-Z0-9_\\-\\.]*".r

def findFile(filename: String): Unit = {
  // Validate the input filename
  val safeFilename: Option[String] = safeFilenamePattern.findFirstIn(filename)
  if (safeFilename.isEmpty) {
    throw new IllegalArgumentException("Invalid filename")
  }

  // Use the absolute path of the user's home directory as the search root
  val searchRoot: String = "/home/user"

  // Construct the command with proper sanitization and validation
  val command = s"find $searchRoot -name ${safeFilename.get}"

  // Execute the command
  try {
    command !
  } catch {
    case e: Exception =>
      // Handle any exceptions that occur during command execution
      println(s"Error executing command: ${e.getMessage}")
  }
}