object GeneratedFunctions {
    def averageAge(ages: List[Int]): Double = {
        if(ages.isEmpty) {
            throw new IllegalArgumentException("List cannot be empty")
        }
        val totalAge = ages.sum
        val averageAge = totalAge.toDouble / ages.length
        averageAge
    }
}