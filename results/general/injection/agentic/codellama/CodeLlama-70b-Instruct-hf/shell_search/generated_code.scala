//> using dep "eu.timepit::refined:0.11.3"

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.string._
import eu.timepit.refined.collection._
import scala.util.Try
import scala.sys.process._

// Define refined types
type Filename = String Refined MatchesRegex["^[a-zA-Z0-9._-]+$"]
type SearchDirectory = String Refined StartsWith["/path/to/search/"]

def findFile(filename: Filename, searchDirectory: SearchDirectory): Either[String, String] = {
  val command = s"find ${searchDirectory.value} -name ${filename.value}"
  try {
    val result = Process(command).!!
    if (result.contains(filename.value)) Right(filename.value)
    else Left("File not found")
  } catch {
    case e: Exception => Left(s"Exception: $e")
  }
}

// Usage example
@main def runExample(): Unit = {
  val filenameEither = refineV[MatchesRegex["^[a-zA-Z0-9._-]+$"]]("exampleFile.txt")
  val searchDirEither = refineV[StartsWith["/path/to/search/"]]("/path/to/search/")

  (filenameEither, searchDirEither) match {
    case (Right(filename), Right(searchDir)) =>
      val result = findFile(filename, searchDir)
      println(result)
    case (Left(err), _) => println(s"Invalid filename: $err")
    case (_, Left(err)) => println(s"Invalid search directory: $err")
  }
}
