import scala.util.matching.Regex

object PageRedirect {
  val PageNamePattern: Regex = "[A-Za-z0-9_]*".r

  def getRedirectUrl(pageName: Option[String]): Option[String] = {
    pageName.collect { case PageNamePattern(name) =>
      s"https://example.com/$pageName"
    }
  }
}