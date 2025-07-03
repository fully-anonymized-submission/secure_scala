import java.net.InetAddress

object PingHost {
  def pingHost(host: String): Either[String, String] = {
    try {
      val inetAddress = InetAddress.getByName(host)
      val isReachable = inetAddress.isReachable(5000) // Timeout set to 5000 milliseconds
      if (isReachable) {
        Right(s"Host $host is reachable.")
      } else {
        Left(s"Host $host is not reachable.")
      }
    } catch {
      case e: Exception => Left(s"Failed to ping host $host: ${e.getMessage}")
    }
  }

  def main(args: Array[String]): Unit = {
    val host = "example.com" // Replace with the host you want to ping
    pingHost(host) match {
      case Right(result) => println(result)
      case Left(error) => println(error)
    }
  }
}