import sys.process._

def ping(host: String): String = {
  val command = s"ping -c 1 $host"
  val result = command.lineStream.mkString("\n")
  result
}