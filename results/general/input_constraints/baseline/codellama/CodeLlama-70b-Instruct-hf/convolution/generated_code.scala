object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
        val kernelSize = kernel.length
        val matrixSize = matrix.length
        val result = Array.fill(matrixSize, matrixSize)(0.0)

        for (i <- 0 until matrixSize - kernelSize + 1) {
            for (j <- 0 until matrixSize - kernelSize + 1) {
                for (m <- 0 until kernelSize) {
                    for (n <- 0 until kernelSize) {
                        result(i + m)(j + n) += matrix(i + m)(j + n) * kernel(m)(n)
                    }
                }
            }
        }

        result
    }
}