package nz.eqs.ck
package services

import java.util.UUID

package object supervisor {

  import service._

  // TODO inject this
  val SupervisorProtocolVersion = 0
  val SupervisorClassName = "ck::services:supervisor"

  val SupervisorClassId: ClassId[SupervisorClass] = ClassId[SupervisorClass](UUID.fromString(SupervisorClassName), SupervisorProtocolVersion)
}
package supervisor {

  import service._
  import appmanager._
  import nz.eqs.ck.services.classes.RootServiceClass

  package commands {
    import scala.reflect.ClassTag

    case object Terminate extends Command[SupervisorClass] {
      override def params: Map[String, ClassTag[_]] = ???
    }

  }

  package events {

    case object SupervisorTerminated extends Event[SupervisorClass]

  }

  trait SupervisorClass extends ServiceClass[SupervisorClass] {

    import supervisor.commands._
    import supervisor.events._

    override val classId: ClassId[SupervisorClass] = SupervisorClassId
    override val parent: ClassId[_] = RootServiceClass.classId
    override val commands: Set[Command[SupervisorClass]] = Set(Terminate)
    override val events: Set[Event[SupervisorClass]] = Set(SupervisorTerminated)
    override val dependencies: Set[ServiceClass[_]] = Set()
  }

  object SupervisorClass extends SupervisorClass

  trait SupervisorService[C <: SupervisorClass] extends Service[C]

  package object controller {

    val SupervisorControllerClassName = "ck::services:supervisor:control"
    val SupervisorControllerClassId: ClassId[SupervisorControllerClass] =
      ClassId[SupervisorControllerClass](UUID.fromString(SupervisorControllerClassName), SupervisorProtocolVersion)

  }

  package controller {

    trait SupervisorControllerClass extends ServiceClass[SupervisorControllerClass] {

      import commands._

      override val classId: ClassId[SupervisorControllerClass] = SupervisorControllerClassId
      override val parent: ClassId[_] = RootServiceClass.classId
      override val commands: Set[Command[SupervisorControllerClass]] = Set()
      override val events: Set[Event[SupervisorControllerClass]] = Set()
      override val dependencies: Set[ServiceClass[_]] = Set()
    }

    object SupervisorControllerClass extends SupervisorControllerClass

  }

}