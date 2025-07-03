import scala.util.{Try, Success, Failure}

// Define a trait for Matrix
sealed trait Matrix {
  def rows: Int
  def cols: Int
  def apply(row: Int, col: Int): Double
}

// Case class for a well-formed rectangular matrix
case class RectangularMatrix(data: Array[Array[Double]]) extends Matrix {
  require(data.nonEmpty && data.forall(_.length == data.head.length), "Matrix must be rectangular")
  override def rows: Int = data.length
  override def cols: Int = data.head.length
  override def apply(row: Int, col: Int): Double = data(row)(col)
}

// Object to create matrices with validation
object Matrix {
  def apply(data: Array[Array[Double]]): Try[RectangularMatrix] = {
    if (data.isEmpty || data.exists(_.isEmpty)) {
      Failure(new IllegalArgumentException("Matrix cannot be empty"))
    } else if (data.forall(_.length == data.head.length)) {
      Success(RectangularMatrix(data))
    } else {
      Failure(new IllegalArgumentException("Matrix must be rectangular"))
    }
  }
}

object GeneratedFunctions {
  def matrixMultiplication(matrixA: Matrix, matrixB: Matrix): Try[Array[Array[Double]]] = {
    if (matrixA.cols != matrixB.rows) {
      return Failure(new IllegalArgumentException("Number of columns in Matrix A must be equal to number of rows in Matrix B"))
    }

    val result = Array.ofDim[Double](matrixA.rows, matrixB.cols)

    for (i <- 0 until matrixA.rows) {
      for (j <- 0 until matrixB.cols) {
        var sum = 0.0
        for (k <- 0 until matrixA.cols) {
          sum += matrixA(i, k) * matrixB(k, j)
        }
        result(i)(j) = sum
      }
    }

    Success(result)
  }
}

// Example usage
object Main extends App {
  val matrixAData = Array(Array(1.0, 2.0), Array(3.0, 4.0))
  val matrixBData = Array(Array(5.0, 6.0), Array(7.0, 8.0))

  val matrixA = Matrix(matrixAData)
  val matrixB = Matrix(matrixBData)

  (for {
    a <- matrixA
    b <- matrixB
    result <- GeneratedFunctions.matrixMultiplication(a, b)
  } yield result) match {
    case Success(res) => println(s"Result: ${res.map(_.mkString(", ")).mkString("\n")}")
    case Failure(e) => println(s"Error: ${e.getMessage}")
  }
}