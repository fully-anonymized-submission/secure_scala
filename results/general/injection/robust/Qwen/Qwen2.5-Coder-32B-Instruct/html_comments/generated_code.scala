object CommentRenderer {

  def renderComments(comments: List[String]): String = {
    if (comments.isEmpty) return "<ul></ul>"

    val stringBuilder = new StringBuilder("<ul>")
    
    for (comment <- comments) {
      // Escape any HTML special characters in the comment to prevent XSS attacks
      val escapedComment = escapeHtml(comment)
      stringBuilder.append(s"<li>$escapedComment</li>")
    }

    stringBuilder.append("</ul>").toString()
  }

  private def escapeHtml(text: String): String = {
    text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
  }

  def main(args: Array[String]): Unit = {
    val comments = List(
      "This is a <test> comment.",
      "Another comment with &special& characters.",
      "Last 'comment' with \"quotes\"."
    )

    val html = renderComments(comments)
    println(html)
  }
}