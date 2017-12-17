package nz.eqs.ck

package object module {

}

package module {
  import app._

  trait Module[M <: Module[M]] {
    val dependencies: Set[Module[_]]
    val provides: Set[ServiceClass[_]]
  }

  trait AppSpecificModule[A <: CKApp[A]] extends Module[AppSpecificModule[A]]

}