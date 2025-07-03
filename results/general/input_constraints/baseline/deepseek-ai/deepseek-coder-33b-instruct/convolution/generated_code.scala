object GeneratedFunctions {
  def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
    val matrixHeight = matrix.length
    val matrixWidth = matrix(0).length
    val kernelHeight = kernel.length
    val kernelWidth = kernel(0).length

    val paddedMatrix = Array.ofDim[Double](matrixHeight + kernelHeight - 1, matrixWidth + kernelWidth - 1)
    for (i <- 0 until matrixHeight; j <- 0 until matrixWidth) {
      paddedMatrix(i + kernelHeight / 2)(j + kernelWidth / 2) = matrix(i)(j)
    }

    val result = Array.ofDim[Double](matrixHeight, matrixWidth)
    for (i <- 0 until matrixHeight; j <- 0 until matrixWidth) {
      var sum = 0.0
      for (k <- 0 until kernelHeight; l <- 0 until kernelWidth) {
        sum += paddedMatrix(i + k)(j + l) * kernel(k)(l)
      }
      result(i)(j) = sum
    }

    result
  }
}