object GeneratedFunctions {
  def fibonacci(n: Int): BigInt = {
    if (n < 0) 0
    else {
      @annotation.tailrec
      def go(n: Int, a: BigInt, b: BigInt): BigInt =
        if (n == 0) a
        else go(n - 1, b, a + b)
      go(n, 0, 1)
    }
  }
}