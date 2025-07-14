// Importing the required Java classes
import java.net.InetAddress
import java.net.UnknownHostException
import scala.util.Try

// The function that pings a host given by the user
def pingHost(hostName: String): String = {
  // Initializing the result
  var result = "Host is not reachable"
  
  // Trying to ping the host
  Try {
    val host = InetAddress.getByName(hostName)
    if (host.isReachable(3000)) {
      result = "Host is reachable"
    }
  } match {
    case _: UnknownHostException => result = "Host unknown"
  }
  
  // Returning the result
  result
}

// Testing the function
println(pingHost("www.google.com"))