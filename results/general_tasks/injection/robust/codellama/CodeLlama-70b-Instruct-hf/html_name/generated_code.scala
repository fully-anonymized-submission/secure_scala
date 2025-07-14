object Greeting {
    def htmlGreeting(name: String): String = {
        val nameRegex = """^([a-zA-Z]+)$""".r
        if(!name.matches(nameRegex.regex)) {
            throw new IllegalArgumentException("Name should be a-z or A-Z")
        }
        s"<p>Hello, $name!</p>"
    }
}