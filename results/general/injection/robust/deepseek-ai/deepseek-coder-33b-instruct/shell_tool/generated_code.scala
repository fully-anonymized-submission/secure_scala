import java.net.InetAddress

def ping(host: String): String = {
  val time = System.currentTimeMillis()
  val address = InetAddress.getByName(host)
  val isReachable = address.isReachable(5000)
  val latency = System.currentTimeMillis() - time
  s"Host: $host is reachable: $isReachable, latency: $latency ms"
}