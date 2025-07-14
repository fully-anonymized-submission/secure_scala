object GeneratedFunctions {
    def averageAge(ages: List[Int]): Double = {
        ages.map(_.toDouble).sum / ages.length
    }
}