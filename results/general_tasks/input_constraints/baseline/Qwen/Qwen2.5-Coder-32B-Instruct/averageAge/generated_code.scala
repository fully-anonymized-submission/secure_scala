object GeneratedFunctions {
    def averageAge(ages: List[Int]): Double = {
        if (ages.isEmpty) 0.0
        else ages.sum.toDouble / ages.length
    }
}