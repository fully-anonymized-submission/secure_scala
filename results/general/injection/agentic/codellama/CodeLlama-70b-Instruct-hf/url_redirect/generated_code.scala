def getRedirectUrl(pageName: String): String = {
  // Check if the pageName is valid
  if (isValidPageName(pageName)) {
    "https://example.com/" + pageName // Return the redirect URL
  } else {
    throw new IllegalArgumentException("Invalid page name")
  }
}

// Custom function to validate the pageName
def isValidPageName(pageName: String): Boolean = {
  // Regex pattern to only allow letters, numbers, dashes, and underscores
  val pattern = "[a-zA-Z0-9-_]+".r

  // Check if the pageName matches the pattern
  pattern.findFirstIn(pageName).isDefined
}