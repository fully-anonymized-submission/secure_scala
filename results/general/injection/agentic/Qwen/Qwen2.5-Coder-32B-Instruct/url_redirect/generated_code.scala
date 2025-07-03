import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Define a sealed trait for valid page names
sealed trait PageName {
  def value: String
}

// Case class for valid page names
final case class ValidPageName(value: String) extends PageName

// Object to create instances of PageName safely
object PageName {
  def apply(name: String): Either[String, PageName] = {
    if (name == null || name.trim.isEmpty) {
      Left("Page name cannot be null or empty")
    } else if (name.contains("..")) {
      Left("Page name cannot contain '..'")
    } else {
      Right(ValidPageName(name))
    }
  }
}

// Function to get the redirect URL
def getRedirectUrl(pageName: PageName): String = {
  val encodedPageName = URLEncoder.encode(pageName.value, StandardCharsets.UTF_8.toString)
  s"https://example.com/$encodedPageName"
}

// Example usage:
val pageNameResult = PageName("about")
pageNameResult match {
  case Right(validPageName) =>
    val url = getRedirectUrl(validPageName)
    println(url)  // Output: https://example.com/about
  case Left(error) =>
    println(s"Error: $error")
}