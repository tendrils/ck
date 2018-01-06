package nz.eqs.ck

package object module {
  type ModuleDependencyMap = Map[Module[_], Set[Module[_]]]
}

package module {
  import app._
  import context._
  import realm._
  import service._

  object Module {
    private var modules: ModuleDependencyMap = Map()
    private def alter[M <: Module[M]](op: (ModuleDependencyMap => ModuleDependencyMap)): Unit = {
      modules = op(modules)
    }

    def register[M <: Module[M]](module: M) = alter(_ + (module -> module.dependencies))

    def registry: Set[Module[_]] = modules.keySet
  }

  trait Module[M <: Module[M]] {
    val dependencies: Set[Module[_]]
    val provides: Set[ServiceClass[_]]
    def init: Unit
    def load(env: Realm): Set[Service[_]]
  }

  trait AppSpecificModuleDef[A <: CKApp[A]] extends Module[AppSpecificModuleDef[A]]

}