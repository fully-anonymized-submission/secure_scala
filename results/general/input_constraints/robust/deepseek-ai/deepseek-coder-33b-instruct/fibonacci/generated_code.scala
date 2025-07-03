object GeneratedFunctions {
  def fibonacci(n: Int): Int = {
    @annotation.tailrec
    def go(n: Int, a: Int, b: Int): Int =
      if (n == 0) a
      else if (n == 1) b
      else go(n - 1, b, a + b)
    require(n >= 0, "n must be greater than or equal to 0")
    go(n, 0, 1)
  }
}