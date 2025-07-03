def getRedirectUrl(pageName: String): String = {
  s"https://example.com/$pageName"
}

// Example usage:
val url = getRedirectUrl("about")
println(url)  // Output: https://example.com/about