import sys.process._

def findFile(filename: String): Unit = {
  val command = s"find / -name $filename"
  command !
}