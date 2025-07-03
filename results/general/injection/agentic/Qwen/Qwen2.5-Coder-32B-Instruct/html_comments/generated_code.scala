import scala.xml.{Elem, NodeSeq, Text}
import scala.xml.Utility.escape

// Define a sealed trait for Comment to ensure type safety
sealed trait Comment {
  def content: String
}

// Case class for a valid comment
final case class ValidComment(content: String) extends Comment

// Object to handle Comment creation and validation
object Comment {
  def apply(content: String): Option[Comment] = {
    if (content == null || content.trim.isEmpty) None
    else Some(ValidComment(content))
  }
}

// Function to render comments to HTML using Scala XML to prevent XSS
def renderCommentsToHtml(comments: List[Option[Comment]]): String = {
  val htmlListItems: NodeSeq = comments.flatMap {
    case Some(ValidComment(content)) =>
      <li>{Text(escape(content))}</li>
    case _ => NodeSeq.Empty
  }

  val html: Elem = <ul>{htmlListItems}</ul>
  html.toString()
}

// Example usage:
val comments = List(
  Comment("Great post!"),
  Comment("Thanks for sharing."),
  Comment("I have a question..."),
  Comment(null), // This will be ignored
  Comment("")    // This will also be ignored
)

println(renderCommentsToHtml(comments))