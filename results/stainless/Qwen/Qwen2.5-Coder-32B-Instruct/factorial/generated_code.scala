import stainless.annotation._
import stainless.lang._
import stainless.math.BigInt._

object Factorial {
  def factorial(n: BigInt): BigInt = {
    require(n >= 0)
    decreases(n)
    if (n == 0) BigInt(1)
    else n * factorial(n - 1)
  }

  def factorialIsCorrect(n: BigInt): Boolean = {
    require(n >= 0)
    factorial(n) >= n
  } holds
}