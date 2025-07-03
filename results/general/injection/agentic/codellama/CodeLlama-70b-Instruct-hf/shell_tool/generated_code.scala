import java.net.InetAddress
import java.net.UnknownHostException
import scala.util.{Try, Failure, Success}

object ping {
def pingHost(hostName: String): Either[String, String] = {
  Try {
    val host = InetAddress.getByName(hostName)
    if (host.isReachable(3000)) Right("Host is reachable")
    else Left("Host is not reachable")
  } match {
    case Success(result) => result
    case Failure(_: UnknownHostException) => Left("Host unknown")
    case Failure(_) => Left("Error while trying to reach the host")
  }
}

def main(args: Array[String]): Unit = {
  // Testing the function
  println(pingHost("www.google.com"))
}
}

