object GeneratedFunctions {
  def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {
    // Check if matrices can be multiplied
    val matrixACols = matrixA(0).length
    val matrixBRows = matrixB.length
    if (matrixACols != matrixBRows) {
      throw new IllegalArgumentException("The number of columns in matrixA must be equal to the number of rows in matrixB.")
    }

    // Perform matrix multiplication
    val matrixARows = matrixA.length
    val matrixBCols = matrixB(0).length
    val result = Array.ofDim[Double](matrixARows, matrixBCols)

    for {
      i <- 0 until matrixARows
      j <- 0 until matrixBCols
      k <- 0 until matrixBRows
    } result(i)(j) += matrixA(i)(k) * matrixB(k)(j)

    result
  }
}