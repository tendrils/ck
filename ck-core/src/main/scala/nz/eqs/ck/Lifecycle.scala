package nz.eqs.ck

object Lifecycle {
  trait Scope
  trait State[Scope]
  trait Error[Scope] extends State[Scope] { val message: String }
  trait Phase[Scope] extends State[Scope] { val order: Int }

  abstract class PhaseOrdering[Scope] extends Ordering[Phase[Scope]]

  final class Main extends Scope

  object Main {
    sealed case class Phase(order: Int) extends Lifecycle.Phase[Main]
    sealed case class Error(message: String) extends Lifecycle.Error[Main]

    // sequential states
    case object Boot extends Main.Phase(0)
    case object Run extends Main.Phase(1)
    case object Shutdown extends Main.Phase(2)
    case object Terminate extends Main.Phase(3)

    // error states
    case class UnhandledException(cause: Throwable) extends Main.Error("Unhandled Exception: " + cause.getClass.getSimpleName) {
      val exceptionMessage: String = cause.getMessage
    }
    case class EscalatedRuntimeError(source: Error[_]) extends Main.Error("Unhandled CK Runtime Error:" + source.message)
  }
}
