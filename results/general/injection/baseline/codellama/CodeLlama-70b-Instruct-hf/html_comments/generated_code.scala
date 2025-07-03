import scala.xml.{Elem, NodeSeq, Text}

object CommentListRenderer {

  // Render a list of user comments into an HTML unordered list
  def renderCommentList(comments: List[String]): Elem = {
    <ul>
      {renderComments(comments)}
    </ul>
  }

  // Render the individual comments as list items
  def renderComments(comments: List[String]): NodeSeq = comments match {
    case Nil => Text("")
    case comment :: Nil => <li>{comment}</li>
    case comment :: moreComments => <li>{comment}</li> ++ renderComments(moreComments)
  }
}