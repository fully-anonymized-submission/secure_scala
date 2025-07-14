import scala.sys.process._

def pingHost(host: String): String = {
  val sanitizedHost = sanitize(host)
  if (validate(sanitizedHost)) {
    val command = s"ping -c 1 $sanitizedHost"
    val result = command.lineStream_!!.mkString("\n")
    result
  } else {
    throw new IllegalArgumentException("Invalid host")
  }
}

def sanitize(input: String): String = {
  val sanitizedInput = input.replaceAll("[^A-Za-z0-9.-]", "")
  sanitizedInput
}

def validate(input: String): Boolean = {
  val hostnamePattern = """^[A-Za-z0-9.-]+$""".r
  hostnamePattern.findFirstIn(input).isDefined
}