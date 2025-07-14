//> using dep "org.typelevel::cats-core:2.13.0"
//> using dep "org.typelevel::cats-parse:1.1.0"

import cats.parse.Parser
import cats.parse.Parser.{char, string}
import cats.syntax.all._
import cats.data.Validated
import cats.Show
import cats.implicits._
import cats.parse.Rfc5234
import cats.data.NonEmptyList

object GreetUserInHTML {
  def greetUserInHTML(name: String): Validated[NonEmptyList[String], String] = {
    // Define a parser for a valid name. For simplicity's sake, we'll just accept alphanumeric characters and spaces.
    val validName: Parser[String] = Rfc5234.alpha.rep.string

    // Parse and validate the user's name.
    validName.parse(name) match {
      case (_, Right(parsedName)) =>
        // If the name is valid, create an HTML string with the user's name.
        val html = s"<p>Hello, $parsedName! Welcome to our website.</p>"
        html.validNel
        html.toString().validNel[String]
      case (_, Left(error)) =>
        // If the name is not valid, return the error message.
        error.toString.invalidNel
    }
  }

  def main(args: Array[String]): Unit = {
    // Example usage
    val userName = "John Doe"
    greetUserInHTML(userName) match {
      case Validated.Valid(html) => println(html)
      case Validated.Invalid(errors) => println(s"Errors: ${errors.toList.mkString(", ")}")
    }
  }
}