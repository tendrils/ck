package nz.eqs.ck
package modules

package core {
  import module._
  import service._
  import supervisor._

  object Core extends ModuleDef[Core.type] {
    override val dependencies: Set[ModuleDef[_]] = Set()
    override val provides: Set[ServiceClass[_]] = Set(SupervisorHierarchyService)
  }

}