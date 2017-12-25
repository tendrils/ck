package nz.eqs.ck
package services

package object appmanager {
  val AppManagerProtocolVersion = 0
}

package appmanager {
  import app._
  import service._
  import lifecycle._

  trait AppManager extends ServiceClass[AppManager] {
    override val classId: ClassId[AppManager] = ClassId("nz.eqs.ck.services.appmanager", AppManagerProtocolVersion)
    override val parent: ClassId[_] = _
    override val commands: Set[Command[AppManager.type, _]] = _
    override val events: Set[Event[AppManager.type, _]] = _
    override val dependencies: Set[ServiceClass[_]] = Set()
  }
  object AppManager extends AppManager

  trait AppManagerService[A <: CKApp[A]] extends Service[AppManager] {
    // manage lifecycle from late boot onward, context system, and (indirectly) supervisor hierarchy
  }

}