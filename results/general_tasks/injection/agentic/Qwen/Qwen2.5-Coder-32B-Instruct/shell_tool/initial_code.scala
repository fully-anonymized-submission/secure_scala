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
      case e: Exception => s"Failed to ping host $host: ${e.getMessage}"
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Please provide a hostname or IP address as an argument.")
    } else {
      val host = args(0)
      println(pingHost(host))
    }
  }
}