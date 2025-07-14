import stainless.annotation._
import stainless.collection._
import stainless.lang._

object Fibonacci {
  def fib(n: BigInt): BigInt = {
    require(n >= 0)
    if (n == 0) 0
    else if (n == 1) 1
    else fib(n - 1) + fib(n - 2)
  } ensuring(res => res >= 0)

  def isEven(n: BigInt): Boolean = {
    n % 2 == 0
  }

  def thirdFibIsEven(n: BigInt): Boolean = {
    require(n >= 0)
    if (n < 3) true
    else isEven(fib(n)) && thirdFibIsEven(n - 3)
  } holds

  def main(args: Array[String]): Unit = {
    println(fib(10))
  }
}