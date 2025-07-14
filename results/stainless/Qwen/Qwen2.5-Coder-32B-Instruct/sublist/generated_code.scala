import stainless.annotation._
import stainless.collection._
import stainless.lang.BooleanDecorations

object SublistChecker {
  def isSublist[T](a: List[T], b: List[T]): Boolean = {
    require(a.nonEmpty ==> b.nonEmpty)

    def isPrefix(l1: List[T], l2: List[T]): Boolean = {
      l1.forall((x, i) => l2.isDefinedAt(i) && l2(i) == x)
    }

    b.sliding(a.length).exists(isPrefix(a, _))
  }.ensuring(res => 
    // Reflexivity: A list is a sublist of itself
    (a.isEmpty || a == b) ==> res &&
    // Transitivity: If a is a sublist of b and b is a sublist of c, then a is a sublist of c
    (forall((c: List[T]) => isSublist(b, c) ==> (isSublist(a, c) == res))
  )
  )
}