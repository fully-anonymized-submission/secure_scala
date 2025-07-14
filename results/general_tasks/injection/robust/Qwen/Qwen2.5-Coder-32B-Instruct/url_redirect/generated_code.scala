object RedirectURLGenerator {
  def generateRedirectURL(pageName: String): Either[String, String] = {
    if (pageName.isEmpty || pageName.contains("..") || pageName.contains("/")) {
      Left("Invalid page name")
    } else {
      Right(s"https://example.com/$pageName")
    }
  }

  def main(args: Array[String]): Unit = {
    // Example usage:
    val pageName = "about"
    generateRedirectURL(pageName) match {
      case Right(url) => println(s"Redirect URL: $url")
      case Left(error) => println(s"Error: $error")
    }
  }
}