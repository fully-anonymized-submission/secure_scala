import stainless.lang._
import stainless.lang.StaticChecks._

def factorial(n: BigInt): BigInt = {
  require(n >= 0)
  
  @annotation.tailrec
  def go(n: BigInt, acc: BigInt): BigInt =
    if (n <= 0) acc
    else go(n - 1, n * acc)
  
  go(n, 1)
}

def factorialVerification(n: BigInt): Unit = {
  require(n >= 0)
  
  val result = factorial(n)
  assert(result >= n)

  def factorialVerification(n: BigInt): Unit = {
    require(n >= 0)
    
    val result = factorial(n)
    assert(result >= n)
  }
}