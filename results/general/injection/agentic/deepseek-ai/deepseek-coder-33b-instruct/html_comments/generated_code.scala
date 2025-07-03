import scala.xml.Utility._

case class Comment(content: String) {
  def sanitized: Comment = Comment(escape(content))
}

case class Comments(list: List[Comment]) {
  def asHtml: String = {
    val htmlComments = list.map(_.sanitized.content).map(comment => s"<li>${comment}</li>").mkString("\n")
    s"""
       |<html>
       |   <body>
       |       <ul>
       |           ${htmlComments}
       |       </ul>
       |   </body>
       |</html>
       |""".stripMargin
  }
}

val comments = Comments(List(Comment("Comment 1"), Comment("Comment 2"), Comment("Comment 3")))

println(comments.asHtml)