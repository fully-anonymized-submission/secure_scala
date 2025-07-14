import stainless.annotation._
import stainless.lang._
import stainless.collection._

object Factorial {

    @library
    def factorial(n: BigInt): BigInt = {
        decreases(n)
        if (n == 0) BigInt(1)
        else n * factorial(n - 1)
    }

    @library
    def factorialCorrectness(n: BigInt): Boolean = {
        def factorialInduction(k: BigInt): Boolean = {
            decreases(k)
            if (k == 0) true
            else {
                val fac = factorial(k)
                fac == k * factorial(k - 1) && factorialInduction(k - 1)
            }
        }
        factorialInduction(n)
    }

    def factorialCorrect(n: BigInt): Boolean = {
        // Checks that the factorial is correct for all n.
        require(n >= 0)
        factorialCorrectness(n)
    }

    def factorialLteN(n: BigInt): Boolean = {
        // Checks that the factorial is always larger or equal to n for all n.
        require(n >= 0)
        factorial(n) >= n
    }

}