object GeneratedFunctions {
    def fibonacci(n: Int): BigInt = {
        require(n >= 0, "n must be a non-negative integer")

        @annotation.tailrec
        def fibHelper(a: BigInt, b: BigInt, count: Int): BigInt = {
            if (count == 0) a
            else fibHelper(b, a + b, count - 1)
        }

        fibHelper(0, 1, n)
    }
}