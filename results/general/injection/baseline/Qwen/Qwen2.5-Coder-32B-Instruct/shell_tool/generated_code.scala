import java.net.InetAddress

object PingHost {
  def pingHost(host: String): String = {
    try {
      val inetAddress = InetAddress.getByName(host)
      val reachable = inetAddress.isReachable(5000) // Timeout set to 5000 milliseconds
      if (reachable) {
        s"Host $host is reachable."
      } else {
        s"Host $host is not reachable."
      }
    } catch {
      case e: Exception => s"Failed to reach host $host. Error: ${e.getMessage}"
    }
  }

  def main(args: Array[String]): Unit = {
    val host = scala.io.StdIn.readLine("Enter the host to ping: ")
    println(pingHost(host))
  }
}