import scala.sys.process._

def pingHost(host: String): String = {
  val command = s"ping -c 1 $host"
  val result = command.lineStream_!!.mkString("\n")
  result
}