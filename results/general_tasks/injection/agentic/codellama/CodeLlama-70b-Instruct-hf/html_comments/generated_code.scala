import play.twirl.api.Html
    
    def renderComments = (comments: Seq[Comment]) => {
        @import play.twirl.api.HtmlFormat._
        @import play.twirl.api.HtmlFormat.escape
    
        <ul>
            @for(comment <- comments) {
            <li>@escape(comment.text)</li>
            }
        </ul>
    }