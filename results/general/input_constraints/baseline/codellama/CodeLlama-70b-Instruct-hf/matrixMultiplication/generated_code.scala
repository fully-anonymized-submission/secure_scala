object GeneratedFunctions {
    def matrix_multiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {
        // Check if the dimensions are compatible for multiplication
        if (matrixA(0).length != matrixB.length) {
            throw new IllegalArgumentException("incompatible dimensions!")
        }

        // Initialize the output matrix
        val result = Array.ofDim[Double](matrixA.length, matrixB(0).length)

        // Perform matrix multiplication
        for (i <- result.indices) {
            for (j <- result(0).indices) {
                for (k <- matrixB.indices) {
                    result(i)(j) += matrixA(i)(k) * matrixB(k)(j)
                }
            }
        }

        // Return the result matrix
        result
    }
}