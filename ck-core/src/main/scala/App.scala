package nz.eqs.ck

package object app {

}

package app {
  import module._

  abstract class CKApp[A: CKApp](services: List[ServiceClass[_]]) extends AppSpecificModule[A] {
    val supervisor: ServiceRef[SupervisionService]
  }

}
