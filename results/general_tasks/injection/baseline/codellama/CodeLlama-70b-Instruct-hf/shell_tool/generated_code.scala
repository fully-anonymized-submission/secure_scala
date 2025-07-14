object PingHost {

    import scala.sys.process._

    def ping(host: String): String = {
        val ping = Seq("ping", "-c", "3", host)
        val res = ping.!!
        res
    }

    def main(args: Array[String]) {
        val host = args(0)
        val result = ping(host)
        if (result.contains("0% packet loss"))
            println("Host is reachable")
        else
            println("Host is not reachable")
    }
}