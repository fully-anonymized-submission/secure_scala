object GeneratedFunctions {
    def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Array[Array[Double]] = {
        if(matrixA.length == 0 || matrixB.length == 0) return Array.empty[Array[Double]]
        if(matrixA(0).length != matrixB.length) throw new IllegalArgumentException("Matrix dimensions must agree")

        val result = Array.ofDim[Double](matrixA.length, matrixB(0).length)

        for(i <- 0 until matrixA.length){
            for(j <- 0 until matrixB(0).length){
                for(k <- 0 until matrixB.length){
                    result(i)(j) += matrixA(i)(k) * matrixB(k)(j)
                }
            }
        }

        result
    }
}