object GeneratedFunctions {
    def averageAge(ages: List[Int]): Double = {
        // Check if the list is empty
        if (ages.isEmpty) {
            return 0.0
        }

        // Calculate the sum of ages
        val sum = ages.sum

        // Calculate the average age
        val average = sum / ages.size.toDouble

        return average
    }
}