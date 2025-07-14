import scala.util.{Try, Success, Failure}

// Define an ADT for a Matrix
sealed trait Matrix {
  def rows: Int
  def cols: Int
  def apply(row: Int, col: Int): Double
}

// Case class for a non-empty matrix
final case class NonEmptyMatrix(data: Vector[Vector[Double]]) extends Matrix {
  require(data.nonEmpty && data.forall(_.length == data.head.length), "Matrix must be non-empty and have consistent row lengths")
  def rows: Int = data.length
  def cols: Int = data.head.length
  def apply(row: Int, col: Int): Double = data(row)(col)
}

// Define an ADT for a Kernel
sealed trait Kernel extends Matrix

// Case class for a non-empty kernel
final case class NonEmptyKernel(data: Vector[Vector[Double]]) extends Kernel {
  require(data.nonEmpty && data.forall(_.length == data.head.length), "Kernel must be non-empty and have consistent row lengths")
  def rows: Int = data.length
  def cols: Int = data.head.length
  def apply(row: Int, col: Int): Double = data(row)(col)
}

object GeneratedFunctions {
  def convolution(matrix: NonEmptyMatrix, kernel: NonEmptyKernel): Try[NonEmptyMatrix] = {
    val matrixRows = matrix.rows
    val matrixCols = matrix.cols
    val kernelRows = kernel.rows
    val kernelCols = kernel.cols

    // Validate that kernel size is smaller than or equal to matrix size
    if (kernelRows > matrixRows || kernelCols > matrixCols) {
      return Failure(new IllegalArgumentException("Kernel size must be smaller than or equal to matrix size"))
    }

    // Calculate padding sizes
    val padHeight = kernelRows / 2
    val padWidth = kernelCols / 2

    // Create a padded matrix
    val paddedMatrix = Vector.tabulate(matrixRows + 2 * padHeight, matrixCols + 2 * padWidth) { (i, j) =>
      if (i >= padHeight && i < matrixRows + padHeight && j >= padWidth && j < matrixCols + padWidth) {
        matrix(i - padHeight, j - padWidth)
      } else {
        0.0
      }
    }

    // Create the result matrix with the same size as the original matrix
    val resultMatrix = Vector.tabulate(matrixRows, matrixCols) { (i, j) =>
      var sum = 0.0
      for (m <- 0 until kernelRows; n <- 0 until kernelCols) {
        sum += paddedMatrix(i + m)(j + n) * kernel(m, n)
      }
      sum
    }

    Success(NonEmptyMatrix(resultMatrix))
  }
}

// Example usage
object Main extends App {
  val matrix = NonEmptyMatrix(Vector(
    Vector(1.0, 2.0, 3.0),
    Vector(4.0, 5.0, 6.0),
    Vector(7.0, 8.0, 9.0)
  ))

  val kernel = NonEmptyKernel(Vector(
    Vector(1.0, 0.0),
    Vector(0.0, 1.0)
  ))

  GeneratedFunctions.convolution(matrix, kernel) match {
    case Success(result) => println(result.data.map(_.mkString(" ")).mkString("\n"))
    case Failure(e) => println(s"Error: ${e.getMessage}")
  }
}