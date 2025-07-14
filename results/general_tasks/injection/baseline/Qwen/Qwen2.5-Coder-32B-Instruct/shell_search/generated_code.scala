import scala.sys.process._

def findFile(filename: String): Unit = {
  val command = Seq("find", ".", "-name", filename)
  command.! match {
    case 0 => println(s"Command executed successfully. Output:")
    case _ => println(s"Command failed.")
  }
  command.!
}

// Example usage:
// findFile("example.txt")