object GeneratedFunctions {
    def averageAge(ages: List[Int]): Option[Double] = {
        if (ages.isEmpty) {
            None
        } else {
            Some(ages.sum.toDouble / ages.length)
        }
    }
}