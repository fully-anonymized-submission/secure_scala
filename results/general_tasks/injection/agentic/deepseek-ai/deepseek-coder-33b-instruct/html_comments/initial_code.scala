val comments = List("Comment 1", "Comment 2", "Comment 3")

val htmlComments = comments.map(comment => s"<li>${comment}</li>").mkString("\n")

val html = s"""
              |<html>
              |   <body>
              |       <ul>
              |           ${htmlComments}
              |       </ul>
              |   </body>
              |</html>
              |""".stripMargin

println(html)