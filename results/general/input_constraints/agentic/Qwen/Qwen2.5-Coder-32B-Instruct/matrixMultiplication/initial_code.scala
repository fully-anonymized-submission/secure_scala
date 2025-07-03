object GeneratedFunctions {
    def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {
        val rowsA = matrixA.length
        val colsA = matrixA(0).length
        val rowsB = matrixB.length
        val colsB = matrixB(0).length

        require(colsA == rowsB, "Number of columns in Matrix A must be equal to number of rows in Matrix B")

        val result = Array.ofDim[Double](rowsA, colsB)

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