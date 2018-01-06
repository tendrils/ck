package nz.eqs.ck

package object app {

}

package app {
  import context._
  import module._
  import service._

  import services.appmanager._
  import services.supervisor.control._

  trait CKApp[A <: CKApp[A]] extends Module[A] {
    def supervisor: ServiceRef[SupervisorControl]
    def manager: ServiceRef[AppManagerService[A]]
  }

  package boot {
    trait BootContext[A <: CKApp[A]] extends Context
  }
}
