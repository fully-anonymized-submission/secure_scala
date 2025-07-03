object GeneratedFunctions {
    def matrix_multiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {
        // Check if multiplication is possible
        if (matrixA.isEmpty || matrixB.isEmpty || matrixA.head.length != matrixB.length) {
            throw new IllegalArgumentException("Matrix dimensions must be compatible for multiplication.")
        }

        val rowsA = matrixA.length
        val colsA = matrixA.head.length
        val colsB = matrixB.head.length

        // Initialize the result matrix with zeros
        val result = Array.ofDim[Double](rowsA, colsB)

        // Perform matrix multiplication
        for (i <- 0 until rowsA) {
            for (j <- 0 until colsB) {
                for (k <- 0 until colsA) {
                    result(i)(j) += matrixA(i)(k) * matrixB(k)(j)
                }
            }
        }

        result
    }
}