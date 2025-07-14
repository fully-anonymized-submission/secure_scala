object GeneratedFunctions {
    // Define a custom exception for invalid input
    case class InvalidFibonacciInputException(message: String) extends Exception(message)

    // Use a sealed trait to represent the result of the Fibonacci computation
    sealed trait FibonacciResult
    final case class FibonacciValue(value: BigInt) extends FibonacciResult
    final case class FibonacciError(message: String) extends FibonacciResult

    // Function to compute the n-th Fibonacci number using an iterative approach
    def fibonacci(n: Int): FibonacciResult = {
        if (n < 0) {
            FibonacciError("Fibonacci is not defined for negative numbers.")
        } else {
            var a: BigInt = 0
            var b: BigInt = 1
            for (_ <- 0 until n) {
                val next = a + b
                a = b
                b = next
            }
            FibonacciValue(a)
        }
    }
}