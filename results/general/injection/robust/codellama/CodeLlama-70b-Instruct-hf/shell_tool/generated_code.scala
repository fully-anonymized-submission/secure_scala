import sys.process._

def pingHost(host: String): String = {
  val pingProcess = Process(s"ping $host")
  val pingResult = pingProcess.!!
  if (pingResult.contains("bytes from") && !pingResult.contains("destination host unreachable")) {
    "Host is reachable"
  } else {
    "Host is not reachable"
  }
}

// Wrap in object for testing
object PingTestApp {
  def main(args: Array[String]): Unit = {
    val testHost = "8.8.8.8"
    val result = pingHost(testHost)
    println(s"Ping result for $testHost: $result")
  }
}
