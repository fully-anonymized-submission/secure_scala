def renderCommentsToHTML(comments: List[String]): String = {
  val htmlComments = comments.map(comment => s"<li>${comment.replace("<", "&lt;").replace(">", "&gt;")}</li>")
  s"<ul>\n${htmlComments.mkString("\n")}\n</ul>"
}