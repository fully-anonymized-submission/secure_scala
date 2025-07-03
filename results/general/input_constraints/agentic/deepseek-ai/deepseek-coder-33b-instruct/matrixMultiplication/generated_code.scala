import scala.util.{Try, Success, Failure}

object GeneratedFunctions {

  def matrixMultiplication(matrixA: Array[Array[Double]], matrixB: Array[Array[Double]]): Try[Array[Array[Double]]] = {
    for {
      rowsA <- Try(matrixA.length)
      colsA <- Try(matrixA(0).length)
      rowsB <- Try(matrixB.length)
      colsB <- Try(matrixB(0).length)
      _     <- Try(if (colsA != rowsB) throw new Exception("Matrices cannot be multiplied"))
      matrixC = Array.ofDim[Double](rowsA, colsB)
    } yield {
      for (i <- 0 until rowsA) {
        for (j <- 0 until colsB) {
          for (k <- 0 until colsA) {
            matrixC(i)(j) += matrixA(i)(k) * matrixB(k)(j)
          }
        }
      }
      matrixC
    }
  }
}