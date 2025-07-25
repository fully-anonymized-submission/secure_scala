import scala.annotation.tailrec

object GeneratedFunctions {
  def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
    require(matrix.nonEmpty && kernel.nonEmpty, "Matrix and kernel must not be empty")
    require(matrix.forall(_.length == matrix(0).length) && kernel.forall(_.length == kernel(0).length),
      "Matrix and kernel must be square")

    val m = matrix.length
    val n = matrix(0).length
    val k = kernel.length

    require(k <= m && k <= n, "Kernel size must be less than or equal to the matrix dimensions")

    val paddedMatrix = Array.ofDim[Double](m + 2 * (k / 2), n + 2 * (k / 2))
    for (i <- 0 until m; j <- 0 until n) {
      paddedMatrix(i + k / 2)(j + k / 2) = matrix(i)(j)
    }

    val result = Array.ofDim[Double](m, n)
    for (i <- k / 2 until m + k / 2; j <- k / 2 until n + k / 2) {
      var sum = 0.0
      for (x <- 0 until k; y <- 0 until k) {
        sum += paddedMatrix(i - k / 2 + x)(j - k / 2 + y) * kernel(x)(y)
      }
      result(i - k / 2)(j - k / 2) = sum
    }

    result
  }
}