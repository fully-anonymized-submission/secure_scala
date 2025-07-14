import io.lemonlabs.uri.Url

object url_redirect {
  def redirectToPage(pageName: String): String = {
    val url = Url.parse("https://example.com") / pageName
    url.toString
  }

  def main(args: Array[String]): Unit = {
    // Example usage
    val pageName = "about"
    println(redirectToPage(pageName))
  }
}
