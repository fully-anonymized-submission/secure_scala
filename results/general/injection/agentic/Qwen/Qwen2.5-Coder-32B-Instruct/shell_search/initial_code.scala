import scala.sys.process._

def findFile(filename: String): Unit = {
  val command = Seq("find", ".", "-name", filename)
  command.!
}

// Example usage:
// findFile("example.txt")