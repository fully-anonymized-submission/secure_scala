import scala.xml.Utility.escape

sealed trait UserName {
  def value: String
}

object UserName {
  def apply(name: String): Either[String, UserName] = {
    if (name.trim.isEmpty) Left("Name cannot be empty")
    else Right(new UserName {
      override def value: String = escape(name)
    })
  }
}

def greetUser(name: UserName): String = {
  s"<p>Hello, ${name.value}!</p>"
}

// Example usage:
val userName = UserName("John <script>alert('XSS')</script>Doe")
userName match {
  case Right(user) => println(greetUser(user))
  case Left(error) => println(s"Error: $error")
}