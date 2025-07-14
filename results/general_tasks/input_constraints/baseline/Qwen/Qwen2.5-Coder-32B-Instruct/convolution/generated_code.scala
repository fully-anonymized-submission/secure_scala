object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
        val kernelHeight = kernel.length
        val kernelWidth = kernel(0).length
        val padHeight = kernelHeight / 2
        val padWidth = kernelWidth / 2

        val paddedMatrix = Array.ofDim[Double](matrix.length + 2 * padHeight, matrix(0).length + 2 * padWidth)

        // Fill the padded matrix with the original matrix values and zeros around it
        for (i <- 0 until matrix.length) {
            for (j <- 0 until matrix(i).length) {
                paddedMatrix(i + padHeight)(j + padWidth) = matrix(i)(j)
            }
        }

        val resultMatrix = Array.ofDim[Double](matrix.length, matrix(0).length)

        // Perform the convolution operation
        for (i <- 0 until matrix.length) {
            for (j <- 0 until matrix(i).length) {
                var sum = 0.0
                for (m <- 0 until kernelHeight) {
                    for (n <- 0 until kernelWidth) {
                        sum += paddedMatrix(i + m)(j + n) * kernel(m)(n)
                    }
                }
                resultMatrix(i)(j) = sum
            }
        }

        resultMatrix
    }
}