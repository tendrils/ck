package nz.eqs.ck

package object module {

}

package module {
  import app._
  import service._

  trait ModuleDef[M <: ModuleDef[M]] {
    val dependencies: Set[ModuleDef[_]]
    val provides: Set[ServiceClass[_]]
  }

  trait AppSpecificModuleDef[A <: CKApp[A]] extends ModuleDef[AppSpecificModuleDef[A]]

}