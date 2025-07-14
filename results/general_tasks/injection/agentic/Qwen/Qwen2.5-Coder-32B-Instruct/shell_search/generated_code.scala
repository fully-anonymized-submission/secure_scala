import scala.sys.process._
import scala.util.matching.Regex

object SafeFileFinder {
  // Define a trait for valid filenames
  sealed trait ValidFilename {
    def value: String
  }

  // Case class for valid filenames
  final case class Filename(value: String) extends ValidFilename

  // Object to create valid filenames
  object Filename {
    private val validFilenamePattern: 
    Regex = """^[a-zA-Z0-9._-]+$""".r

    def apply(filename: String): Option[Filename] = {
      if (validFilenamePattern.matches(filename)) Some(new Filename(filename))
      else None
    }
  }

  def findFile(filename: ValidFilename): Unit = {
    val command = Seq("find", ".", "-name", filename.value)
    command.!
  }

  // Example usage
  def main(args: Array[String]): Unit = {
    Filename("example.txt") match {
      case Some(validFilename) => findFile(validFilename)
      case None => println("Invalid filename provided.")
    }
  }
}