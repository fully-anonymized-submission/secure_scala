object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {
        // Verify that the input matrix and kernel are not empty
        if (matrix.isEmpty || kernel.isEmpty) {
            return Array.empty[Array[Double]]
        }

        // Compute the size of the matrix and kernel
        val matrixSize = matrix.length
        val kernelSize = kernel.length

        // Initialize a new matrix of the same size as the input matrix
        val result = Array.ofDim[Double](matrixSize, matrixSize)

        // Initialize the padding size
        val padSize = (kernelSize - 1) / 2

        // Pad the input matrix with zeros to preserve the original input size
        val paddedMatrix = Array.ofDim[Double](matrixSize + 2 * padSize, matrixSize + 2 * padSize)
        for (row <- 0 until matrixSize + 2 * padSize) {
            for (col <- 0 until matrixSize + 2 * padSize) {
                if (row >= padSize && row < matrixSize + padSize && col >= padSize && col < matrixSize + padSize) {
                    paddedMatrix(row)(col) = matrix(row - padSize)(col - padSize)
                } else {
                    paddedMatrix(row)(col) = 0.0
                }
            }
        }

        // Iterate over the rows of the padded matrix
        for (row <- padSize until matrixSize + padSize) {
            // Iterate over the columns of the padded matrix
            for (col <- padSize until matrixSize + padSize) {
                if (row >= padSize && row < matrixSize + padSize && col >= padSize && col < matrixSize + padSize) {
                    // Perform the convolution operation
                    var sum = 0.0
                    for (kRow <- 0 until kernelSize) {
                        for (kCol <- 0 until kernelSize) {
                            sum += kernel(kRow)(kCol) * paddedMatrix(row - kRow)(col - kCol)
                        }
                    }
                    result(row - padSize)(col - padSize) = sum
                } else {
                    // Set the value of the result at the current index to 0
                    result(row - padSize)(col - padSize) = 0.0
                }
            }
        }

        return result
    }
}