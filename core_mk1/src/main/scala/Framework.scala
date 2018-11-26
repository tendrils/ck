package nz.eqs.ck

package object framework {
  // TODO inject contextual version info

  val RootFrameworkVersionNumber = 0
  val RootFrameworkVersionName = "0.0.1"

  implicit val RootFrameworkVersion = FrameworkVersion[RootFramework](RootFrameworkVersionNumber, RootFrameworkVersionName)
}

package framework {
  import meta._
  import context._
  import module._
  import service._

  import modules.core.services._
  import appmanager._
  import supervisor.control._

  object RootFramework extends RootFramework {

  }

  sealed trait RootFramework extends Framework[FrameworkVersion[RootFramework]] {
    // TODO the root classloader should be injected
    override val classLoader: ClassLoader = ClassLoader.getSystemClassLoader

  }

  object Framework {
    val root: Framework[RootFramework] = new RootFramework {
      override val version: FrameworkVersion[FrameworkVersion[RootFramework]] = _
      override val subordinates: Set[Framework[_]] = _
    }
  }

  trait Framework[F <: Framework[F]] {
    val version: FrameworkVersion[F]
    val classLoader: ClassLoader
    val modules: ModuleRegistry = new ModuleRegistry {} //
    val subordinates: Set[Framework[_]]
  }

  case class FrameworkVersion[F <: Framework[F]](override val seq: Long, override val name: String)
    extends Version[Framework[F]](seq, name)

  trait CKApp[A <: CKApp[A]] extends Module[A] {
    def supervisor: ServiceRef[SupervisorControl]
    def manager: ServiceRef[AppManagerService[A]]
  }

  package boot {
    trait BootContext[A <: CKApp[A]] extends Context
  }
}
