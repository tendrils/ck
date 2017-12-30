package nz.eqs.ck

package modules.core {
  import app._
  import module._
  import service._

  import services.appmanager._
  import services.supervisor.control._

  trait Core extends ModuleDef[Core] {
    override val dependencies: Set[ModuleDef[_]] = Set()
    override val provides: Set[ServiceClass[_]] = Set(AppManager, SupervisorControl)
    override def init: Unit = ???
    override def load(): Unit = ???
  }

  object Core extends Core {
  }

  trait CKDefaultAppManager[A <: CKApp[A]]
}