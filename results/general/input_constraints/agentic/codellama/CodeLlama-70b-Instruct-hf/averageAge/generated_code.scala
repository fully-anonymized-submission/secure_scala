object GeneratedFunctions {
    def averageAge(ages: List[Int]): Double = {
        if (ages.isEmpty) throw new IllegalArgumentException("The list of ages cannot be empty")
        if (ages.contains(null)) throw new IllegalArgumentException("The list of ages cannot contain null values")
        if (ages.exists(age => age < 0)) throw new IllegalArgumentException("Ages cannot be negative")
        val result = ages.map(_.toDouble).sum / ages.length
        if (result.isInfinity || result.isNaN) throw new IllegalArgumentException("The result is not a finite number")
        result
    }
}