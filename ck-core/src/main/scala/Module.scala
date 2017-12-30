package nz.eqs.ck

package object module {

}

package module {
  import app._
  import context._
  import service._

  trait ModuleDef[M <: ModuleDef[M]] {
    val dependencies: Set[ModuleDef[_]]
    val provides: Set[ServiceClass[_]]
    def init: Unit
    def load()
  }

  trait AppSpecificModuleDef[A <: CKApp[A]] extends ModuleDef[AppSpecificModuleDef[A]]

}