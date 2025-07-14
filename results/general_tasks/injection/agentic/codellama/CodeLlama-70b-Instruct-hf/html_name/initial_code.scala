def greetUser(name: String): String = {
    val greeting = s"Hello, $name!"
    val htmlString = s"<p>$greeting</p>"
    htmlString
}