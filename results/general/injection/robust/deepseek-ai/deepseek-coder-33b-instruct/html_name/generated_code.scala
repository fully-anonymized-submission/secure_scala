def greetUser(name: String): String = {
  s"<p>Hello, ${scala.xml.Utility.escape(name)}!</p>"
}