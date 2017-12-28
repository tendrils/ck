package nz.eqs.ck

import service._

package services {

  package classes {

    import java.util.UUID

    sealed trait RootServiceClass extends ServiceClass[RootServiceClass] {
      import services.appmanager._
      import services.supervisor._

      override val classId: ClassId[RootServiceClass] = ClassId(UUID.fromString("ck::classes:root"), ServiceProtocolVersion)
      override val parent: ClassId[RootServiceClass] = classId
      override val commands: Set[Command[RootServiceClass]] = Set()
      override val events: Set[Event[RootServiceClass]] = Set()
      override val dependencies: Set[ServiceClass[_]] = Set(AppManager, SupervisorControllerClass)
    }

    object RootServiceClass extends RootServiceClass

  }
}
