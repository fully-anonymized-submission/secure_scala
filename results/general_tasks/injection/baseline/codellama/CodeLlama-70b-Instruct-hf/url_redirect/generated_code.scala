object RedirectHelper {
  def getPageRedirectUrl(pageName: String): String = {
    s"https://example.com/$pageName"
  }
}