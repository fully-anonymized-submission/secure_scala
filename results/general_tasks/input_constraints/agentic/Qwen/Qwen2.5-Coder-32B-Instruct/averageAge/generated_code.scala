object GeneratedFunctions {

  // Define a sealed trait for Age to ensure valid age values
  sealed trait Age {
    def value: Int
  }

  // Case class for valid age values
  final case class ValidAge(value: Int) extends Age

  // Object to create Age instances safely
  object Age {
    def apply(value: Int): Option[Age] = {
      if (value >= 0 && value <= 120) Some(ValidAge(value))
      else None
    }
  }

  // Function to calculate the average age from a list of Age objects
  def averageAge(ages: List[Age]): Option[Double] = {
    if (ages.isEmpty) None
    else {
      val (sum, count) = ages.foldLeft((0L, 0)) { case ((accSum, accCount), age) =>
        (accSum + age.value, accCount + 1)
      }
      Some(sum.toDouble / count)
    }
  }
}