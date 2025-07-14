object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
        val matrixRows = matrix.length
        val matrixCols = matrix.head.length
        val kernelRows = kernel.length
        val kernelCols = kernel.head.length

        // Calculate padding sizes
        val padHeight = kernelRows / 2
        val padWidth = kernelCols / 2

        // Create a padded matrix
        val paddedMatrix = Array.ofDim[Double](matrixRows + 2 * padHeight, matrixCols + 2 * padWidth)

        // Fill the padded matrix with the original matrix values and zeros around it
        for (i <- 0 until matrixRows) {
            for (j <- 0 until matrixCols) {
                paddedMatrix(i + padHeight)(j + padWidth) = matrix(i)(j)
            }
        }

        // Create the result matrix with the same size as the original matrix
        val resultMatrix = Array.ofDim[Double](matrixRows, matrixCols)

        // Perform the convolution
        for (i <- 0 until matrixRows) {
            for (j <- 0 until matrixCols) {
                for (m <- 0 until kernelRows) {
                    for (n <- 0 until kernelCols) {
                        resultMatrix(i)(j) += paddedMatrix(i + m)(j + n) * kernel(m)(n)
                    }
                }
            }
        }

        resultMatrix
    }
}