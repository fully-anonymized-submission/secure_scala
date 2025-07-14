import scala.collection.mutable.ArrayBuffer

object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {

        val matrixRows = matrix.length
        val matrixCols = matrix(0).length

        val kernelRows = kernel.length
        val kernelCols = kernel(0).length

        val resultRows = matrixRows - kernelRows + 1
        val resultCols = matrixCols - kernelCols + 1

        val result = Array.ofDim[Double](resultRows, resultCols)

        for (i <- 0 until resultRows) {
            for (j <- 0 until resultCols) {
                var sum = 0.0
                for (m <- 0 until kernelRows) {
                    for (n <- 0 until kernelCols) {
                        sum += matrix(i + m)(j + n) * kernel(m)(n)
                    }
                }
                result(i)(j) = sum
            }
        }

        val paddedResult = ArrayBuffer.fill(matrixRows, matrixCols)(0.0)
        for (i <- 0 until resultRows) {
            for (j <- 0 until resultCols) {
                paddedResult(i)(j) = result(i)(j)
            }
        }

        paddedResult.toArray
    }
}