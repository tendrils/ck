package nz.eqs.ck

import scala.util.Try

package object lifecycle {
  import lifecycle._
  type Stage[S <: Scope[_], P <: Phase[S]] = P => Try[State[S]]
}

package lifecycle {
  import service._

  trait Scope[Parent <: Scope[_]]
  trait State[Scope]
  trait Error[Scope] extends State[Scope] { val message: String }
  trait Phase[Scope] extends State[Scope] { val order: Int }

  trait PhaseOrdering[Scope] extends Ordering[Phase[Scope]]

  trait LifecycleManager[Scope] extends ServiceClass[LifecycleManager[Scope]] {
  }

  object Main extends Scope[Nothing] {
    sealed case class Phase(order: Int) extends lifecycle.Phase[Main.type]
    sealed case class Error(message: String) extends lifecycle.Error[Main.type]

    // sequential states
    case object Boot extends Main.Phase(0)
    case object Run extends Main.Phase(1)
    case object Shutdown extends Main.Phase(2)
    case object Terminate extends Main.Phase(3)

    // error states
    case class UnhandledException(cause: Throwable) extends Main.Error("Unhandled Exception: " + cause.getClass.getSimpleName) {
      val exceptionMessage: String = cause.getMessage
    }
    case class EscalatedRuntimeError(source: lifecycle.Error[_]) extends Main.Error("Unhandled CK Runtime Error:" + source.message)
  }

}