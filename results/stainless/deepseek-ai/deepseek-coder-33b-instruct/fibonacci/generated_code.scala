import stainless.lang._
import stainless.annotation._
import stainless.collection._

object Fibonacci {

  def fib(n: BigInt): BigInt = {
    require(n >= 0)

    @tailrec
    def fibHelper(a: BigInt, b: BigInt, n: BigInt): BigInt = {
      require(n >= 0)
      n match {
        case BigInt(0) => a
        case BigInt(1) => b
        case _ => fibHelper(b, a + b, n - 1)
      }
    }

    fibHelper(0, 1, n)
  }

  @extern
  def even(n: BigInt): Boolean = {
    require(n >= 0)
    n % BigInt(2) == BigInt(0)
  }

  @extern
  def isFibEven(n: BigInt): Boolean = {
    require(n >= 0)
    even(fib(n * 3))
  }

  @law
  def fib_sum_law(a: BigInt, b: BigInt) = {
    require(a >= 0 && b >= 0)
    fib(a + 2) == fib(a + 1) + fib(a)
  }

  @law
  def even_fib(n: BigInt) = {
    require(n >= 0)
    even(fib(n)) == isFibEven(n)
  }

  @law
  def fib_starts_with_0_1() = {
    fib(0) == 0 && fib(1) == 1
  }
}