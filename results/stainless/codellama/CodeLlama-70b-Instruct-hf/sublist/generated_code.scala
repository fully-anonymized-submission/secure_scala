import stainless.lang._

object Sublist {

  def isSublist[T](l1: List[T], l2: List[T])(implicit ev: Eq[T]): Boolean = {
    def isSublistRec(l1: List[T], l2: List[T], i: BigInt): Boolean = {
      require(i >= 0)
      if (l1.isEmpty) true
      else if (l1.head == l2(i.toInt) && isSublistRec(l1.tail, l2, i + 1)) true
      else if (l2.isEmpty) false
      else isSublistRec(l1, l2.tail, i + 1)
    }

    isSublistRec(l1, l2, 0)
  }

  def reflexivity[T](l: List[T])(implicit ev: Eq[T]): Boolean = {
    isSublist(l, l) == true
  }.holds

  def transitivity[T](l1: List[T], l2: List[T], l3: List[T])(implicit ev: Eq[T]): Boolean = {
    if (isSublist(l1, l2) && isSublist(l2, l3)) {
      isSublist(l1, l3) == true
    } else {
      true
    }
  }.holds

}