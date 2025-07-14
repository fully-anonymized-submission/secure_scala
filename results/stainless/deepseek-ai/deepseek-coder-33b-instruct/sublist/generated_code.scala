def isSublist[A](list1: List[A], list2: List[A]): Boolean = 
  list2.sliding(list1.length).exists(_ == list1)

def isSublistTransitive[A](list1: List[A], list2: List[A], list3: List[A]): Boolean = 
  isSublist(list1, list2) && isSublist(list2, list3) ==> isSublist(list1, list3)

def isSublistReflexive[A](list1: List[A]): Boolean = 
  isSublist(list1, list1)