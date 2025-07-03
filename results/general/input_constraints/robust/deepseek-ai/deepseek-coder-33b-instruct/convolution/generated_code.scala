object GeneratedFunctions {

  def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
    val (n, m) = (matrix.length, matrix.head.length)
    val (k, l) = (kernel.length, kernel.head.length)
    val a = n + k - 1
    val b = m + l - 1
    val paddedMatrix = Array.ofDim[Double](a, b)

    for (i <- 0 until n; j <- 0 until m) {
      paddedMatrix(i + (k / 2) until i + (k / 2) + k)(j + (l / 2) until j + (l / 2) + l) = matrix(i)(j)
    }

    val result = Array.ofDim[Double](n, m)

    for (i <- 0 until n; j <- 0 until m) {
      result(i)(j) = paddedMatrix(i until i + k)(j until j + l).zipWithIndex.map { case (row, k) =>
        row.zipWithIndex.map { case (el, l) => el * kernel(k)(l) }.sum
      }.sum
    }

    result
  }
}