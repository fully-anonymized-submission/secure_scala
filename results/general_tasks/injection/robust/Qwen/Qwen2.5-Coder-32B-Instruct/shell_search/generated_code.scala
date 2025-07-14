import scala.sys.process._
import java.io.File

object FileFinder {
  def findFile(filename: String): Either[String, List[String]] = {
    if (filename.isEmpty) {
      Left("Filename cannot be empty")
    } else {
      try {
        val command = Seq("find", ".", "-name", filename)
        val output = command.!!
        val files = output.split("\n").map(_.trim).filterNot(_.isEmpty).toList
        Right(files)
      } catch {
        case e: Exception => Left(s"Error executing command: ${e.getMessage}")
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val filename = "example.txt"
    findFile(filename) match {
      case Right(files) =>
        if (files.isEmpty) {
          println(s"No files found with the name '$filename'")
        } else {
          println(s"Files found with the name '$filename':")
          files.foreach(println)
        }
      case Left(error) =>
        println(s"An error occurred: $error")
    }
  }
}