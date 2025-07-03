object GeneratedFunctions {
    def fibonacci(n: Int): Int = {
      if (n <= 0) return 0
      if (n == 1) return 1
      var a = 0
      var b = 1
      for (i <- 2 to n) {
        val c = a + b
        a = b
        b = c
      }
      b
    }
}