package ck

import ck.module.AppSpecificModule

abstract class CKApp[A: CKApp](services: List[AppSpecificService[A,_]]) extends AppSpecificModule[A,A] {
  val supervisor: ServiceRef[SupervisionService]
}

