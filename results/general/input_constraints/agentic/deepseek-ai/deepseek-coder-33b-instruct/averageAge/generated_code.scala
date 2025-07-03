import scala.util.{Try, Success, Failure}

sealed trait AverageResult
case class ValidAverage(value: Double) extends AverageResult
case class InvalidAverage(message: String) extends AverageResult

object GeneratedFunctions {
  def averageAge(ages: List[Int]): AverageResult = {
    if (ages.isEmpty) return InvalidAverage("List of ages cannot be empty.")

    Try {
      val sum = ages.foldLeft(0L){ case (total, age) => total + age }
      ValidAverage((sum.toDouble / ages.length).formatted("%.2f").toDouble)
    } match {
      case Success(average) => average
      case Failure(_) => InvalidAverage("An error occurred while calculating the average age.")
    }
  }
}