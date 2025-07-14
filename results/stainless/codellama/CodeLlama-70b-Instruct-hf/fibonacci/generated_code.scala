import stainless.lang._
object Fibonacci {
  def fibonacci(n: BigInt): BigInt = {
    require(n >= 0)
    if (n == 0) {
      BigInt(0)
    } else if (n == 1) {
      BigInt(1)
    } else {
      fibonacci(n - 1) + fibonacci(n - 2)
    }
  }

  def isEven(n: BigInt): Boolean = {
    n % 2 == 0
  }

  def fibonacciCorrect(n: BigInt): Boolean = {
    require(n >= 0)
    fibonacci(n) == fibonacci(n - 1) + fibonacci(n - 2)
  }.holds

  def every3rdFibonacciEven(n: BigInt): Boolean = {
    require(n >= 0)
    isEven(fibonacci(3 * n))
  }.holds
}