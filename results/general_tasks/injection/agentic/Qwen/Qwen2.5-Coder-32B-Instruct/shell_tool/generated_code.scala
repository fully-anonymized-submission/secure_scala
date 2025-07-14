import java.net.{InetAddress, UnknownHostException}
import scala.util.{Failure, Success, Try}

// Define an ADT for the result of the ping operation
sealed trait PingResult
case class Reachable(host: String) extends PingResult
case class Unreachable(host: String) extends PingResult
case class PingError(host: String, message: String) extends PingResult

object PingHost {

  // Validate the host input to ensure it is a valid hostname or IP address
  def validateHost(host: String): Either[String, String] = {
    if (host.matches("^(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$|^(?:\\d{1,3}\\.){3}\\d{1,3}$")) {
      Right(host)
    } else {
      Left(s"Invalid host format: $host")
    }
  }

  // Function to ping a host with proper validation and exception handling
  def pingHost(host: String, timeout: Int = 5000): PingResult = {
    validateHost(host) match {
      case Left(error) => PingError(host, error)
      case Right(validHost) =>
        Try(InetAddress.getByName(validHost)) match {
          case Failure(_: UnknownHostException) => PingError(host, "Unknown host")
          case Failure(e) => PingError(host, "Network error")
          case Success(inetAddress) =>
            Try(inetAddress.isReachable(timeout)) match {
              case Success(true) => Reachable(host)
              case Success(false) => Unreachable(host)
              case Failure(_) => PingError(host, "Network error")
            }
        }
    }
  }

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      println("Please provide a hostname or IP address as an argument.")
    } else {
      val host = args(0)
      pingHost(host) match {
        case Reachable(h) => println(s"Host $h is reachable.")
        case Unreachable(h) => println(s"Host $h is not reachable.")
        case PingError(h, msg) => println(s"Failed to ping host $h: $msg")
      }
    }
  }
}