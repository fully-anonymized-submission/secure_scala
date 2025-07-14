val comments = List("Comment 1", "Comment 2", "Comment 3") // Replace this with your actual comments

val htmlCode = s"""
  <!DOCTYPE html>
  <html>
  <body>

  <ul>
    ${comments.map(comment => s"<li>$comment</li>").mkString("\n")}
  </ul>

  </body>
  </html>
"""

println(htmlCode)