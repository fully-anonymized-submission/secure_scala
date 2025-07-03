object GeneratedFunctions {
    def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Either[String, Array[Array[Double]]] = {
        val numRowsA = matrixA.length
        val numColsA = if (numRowsA > 0) matrixA(0).length else 0
        val numRowsB = matrixB.length
        val numColsB = if (numRowsB > 0) matrixB(0).length else 0

        if (numColsA != numRowsB) {
            Left(s"Cannot multiply matrices: The number of columns in matrix A ($numColsA) must equal the number of rows in matrix B ($numRowsB).")
        } else {
            val resultMatrix = Array.ofDim[Double](numRowsA, numColsB)

            for (i <- 0 until numRowsA) {
                for (j <- 0 until numColsB) {
                    var sum = 0.0
                    for (k <- 0 until numColsA) {
                        sum += matrixA(i)(k) * matrixB(k)(j)
                    }
                    resultMatrix(i)(j) = sum
                }
            }

            Right(resultMatrix)
        }
    }
}