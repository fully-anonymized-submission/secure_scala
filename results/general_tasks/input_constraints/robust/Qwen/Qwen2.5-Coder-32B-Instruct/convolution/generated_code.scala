object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
        require(matrix.nonEmpty && matrix.forall(_.length == matrix.head.length), "Matrix must be non-empty and rectangular")
        require(kernel.nonEmpty && kernel.forall(_.length == kernel.head.length), "Kernel must be non-empty and rectangular")
        require(kernel.length % 2 == 1 && kernel.head.length % 2 == 1, "Kernel dimensions must be odd")

        val matrixHeight = matrix.length
        val matrixWidth = matrix.head.length
        val kernelHeight = kernel.length
        val kernelWidth = kernel.head.length

        val padHeight = kernelHeight / 2
        val padWidth = kernelWidth / 2

        // Zero-padding the matrix
        val paddedMatrix = Array.ofDim[Double](matrixHeight + 2 * padHeight, matrixWidth + 2 * padWidth)
        for (i <- 0 until matrixHeight; j <- 0 until matrixWidth) {
            paddedMatrix(i + padHeight)(j + padWidth) = matrix(i)(j)
        }

        // Convolution operation
        val resultMatrix = Array.ofDim[Double](matrixHeight, matrixWidth)
        for (i <- 0 until matrixHeight; j <- 0 until matrixWidth) {
            var sum = 0.0
            for (m <- 0 until kernelHeight; n <- 0 until kernelWidth) {
                sum += paddedMatrix(i + m)(j + n) * kernel(m)(n)
            }
            resultMatrix(i)(j) = sum
        }

        resultMatrix
    }
}