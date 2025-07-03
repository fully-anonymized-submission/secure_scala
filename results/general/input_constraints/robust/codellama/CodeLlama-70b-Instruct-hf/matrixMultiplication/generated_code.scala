object GeneratedFunctions {
    def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {

      // Check if the dimensions are compatible
      if (matrixA.length == 0 || matrixB.length == 0 || matrixA(0).length != matrixB.length) {
        throw new Exception("Incompatible matrix dimensions")
      }

      // Initialize the result matrix
      val result = Array.ofDim[Double](matrixA.length, matrixB(0).length)

      for (i <- 0 until matrixA.length) {
        for (j <- 0 until matrixB(0).length) {
          for (k <- 0 until matrixB.length) {
            result(i)(j) += matrixA(i)(k) * matrixB(k)(j)
          }
        }
      }

      result
    }
}