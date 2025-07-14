object GeneratedFunctions {
    def convolution(matrix: Array[Array[Double]], kernel: Array[Array[Double]]): Array[Array[Double]] = {

        // Validate the input matrix
        if (matrix.isEmpty || matrix(0).isEmpty) {
            throw new IllegalArgumentException("Invalid input matrix")
        }

        // Validate the kernel
        if (kernel.isEmpty || kernel(0).isEmpty) {
            throw new IllegalArgumentException("Invalid kernel")
        }

        // Get the matrix and kernel sizes
        val matrixRows = matrix.length
        val matrixCols = matrix(0).length
        val kernelRows = kernel.length
        val kernelCols = kernel(0).length

        // Calculate the result size
        val resultRows = matrixRows - kernelRows + 1
        val resultCols = matrixCols - kernelCols + 1

        // Validate the result size
        if (resultRows < 0 || resultCols < 0) {
            throw new IllegalArgumentException("Invalid result size")
        }

        // Create a zero-padded result matrix
        val result = Array.fill(resultRows, resultCols)(0.0)

        // Perform convolution
        for (i <- 0 until resultRows) {
            for (j <- 0 until resultCols) {
                var sum = 0.0
                for (m <- 0 until kernelRows) {
                    for (n <- 0 until kernelCols) {
                        // Ensure indices are within bounds
                        if (i + m < matrixRows && j + n < matrixCols) {
                            sum += matrix(i + m)(j + n) * kernel(m)(n)
                        }
                    }
                }
                result(i)(j) = sum
            }
        }

        result
    }
}