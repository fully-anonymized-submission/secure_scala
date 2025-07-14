def greetUser(name: String): String = {
  // Ensure the name is not null or empty
  require(name != null && name.trim.nonEmpty, "Name must not be null or empty")

  // Escape any HTML special characters in the name to prevent XSS attacks
  val escapedName = name.replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                        .replace("'", "&#039;")

  // Return the greeting wrapped in a paragraph tag
  s"<p>Hello, $escapedName!</p>"
}