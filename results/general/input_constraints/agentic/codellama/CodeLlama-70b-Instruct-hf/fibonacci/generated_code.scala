object GeneratedFunctions {
    def fibonacci(n: Int): Int = {
        if (n <= 1) return n
        var a = 0
        var b = 1

        for (i <- 1 to n - 1) {
            val temp = a
            a = b
            b = temp + b
        }
        return b
    }
}