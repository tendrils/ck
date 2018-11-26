package nz.eqs.ck

import nz.eqs.ck.data.SemanticDomain

package object module {
  type ModuleDependencyMap = Map[Module[_], Set[SemanticDomain[_]]]
}

package module {
  import framework._
  import context._
  import data._
  import realm._
  import service._

  trait ModuleRegistry {
    private var modules: ModuleDependencyMap = Map()
    private def alter[M <: Module[M]](op: ModuleDependencyMap => ModuleDependencyMap): Unit = {
      modules = op(modules)
    }

    def register[M <: Module[M]](m: M) = alter(_ + (m -> m.defines))

    def registry: Set[Module[_]] = modules.keySet
  }

  trait Module[M <: Module[M]] {
    val requires: Set[SemanticDomain[_]]
    val defines: Set[SemanticDomain[_]]
    val provides: Set[Protocol[_,_]]
    def init: Unit
    def load(env: Realm): Set[Service[_]]
  }

  trait AppSpecificModuleDef[A <: CKApp[A]] extends Module[AppSpecificModuleDef[A]]

}