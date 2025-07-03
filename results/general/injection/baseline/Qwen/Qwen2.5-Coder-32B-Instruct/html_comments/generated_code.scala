def renderCommentsToHtml(comments: List[String]): String = {
  val htmlListItems = comments.map(comment => s"<li>$comment</li>").mkString("\n")
  s"<ul>\n$htmlListItems\n</ul>"
}

// Example usage:
val comments = List("Great post!", "Thanks for sharing.", "I have a question...")
println(renderCommentsToHtml(comments))