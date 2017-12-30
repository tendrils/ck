package nz.eqs.ck

import java.util.UUID

import service._

package services {

  package object classes {
    val RootServiceClassName: String = "ck::classes:root"
  }

  package classes {

    sealed trait RootServiceClass extends ServiceClass[RootServiceClass] {

      import services.appmanager._
      import services.supervisor.control._

      override val classId: ClassId[RootServiceClass] = ClassId(UUID.fromString(RootServiceClassName), ServiceProtocolVersion)
      override val parent: ClassId[RootServiceClass] = classId
      override val commands: Set[CommandDescriptor[RootServiceClass]] = Set()
      override val events: Set[EventDescriptor[RootServiceClass]] = Set()
      override val dependencies: Set[ServiceClass[_]] = Set(AppManager, SupervisorControl)
    }

    object RootServiceClass extends RootServiceClass

  }

  package object appmanager {
    val AppManagerProtocolVersion = 0
    val AppManagerClassName: String = "ck::services:appmanager"
  }

  package appmanager {

    import app._
    import lifecycle._

    trait AppManager extends ServiceClass[AppManager] {
      override val classId: ClassId[AppManager] = ClassId(UUID.fromString(AppManagerClassName), AppManagerProtocolVersion)
      override val parent: ClassId[_] = _
      override val commands: Set[CommandDescriptor[AppManager]] = _
      override val events: Set[EventDescriptor[AppManager]] = _
      override val dependencies: Set[ServiceClass[_]] = Set()
    }

    object AppManager extends AppManager

    trait AppManagerService[A <: CKApp[A]] extends Service[AppManager] {
      // manage lifecycle from late boot onward, context system, and (indirectly) supervisor hierarchy
    }

  }

  package object supervisor {

    // TODO inject this
    val SupervisorProtocolVersion = 0
    val SupervisorClassName = "ck::services:supervisor"

    val SupervisorClassId: ClassId[Supervisor] = ClassId[Supervisor](UUID.fromString(SupervisorClassName), SupervisorProtocolVersion)
  }
  package supervisor {

    import appmanager._
    import services.classes._

    package commands {

      import scala.reflect.ClassTag

      case object Terminate extends CommandDescriptor[Supervisor] {
        override def params: Map[String, ClassTag[_]] = ???
      }

    }

    package events {
      import scala.reflect.ClassTag

      case object SupervisorTerminated extends EventDescriptor[Supervisor] {
        override val params: Map[String, ClassTag[_]] = Map("supervisorId" -> ClassTag[ServiceId[_]], "exitStatus" -> ClassTag[String]).asInstanceOf[Map[String,ClassTag[_]]]
      }

    }

    // Service Definition: Supervisor
    //
    // encapsulates an instance of whatever entity is used to perform hierarchical
    // supervision in the underlying execution platform (e.g. akka supervisors),
    // and relay commands and events between the supervisor control interface and
    // the underlying supervisor
    //
    trait Supervisor extends ServiceClass[Supervisor] {

      import supervisor.commands._
      import supervisor.events._

      override val classId: ClassId[Supervisor] = SupervisorClassId
      override val parent: ClassId[_] = RootServiceClass.classId
      override val commands: Set[CommandDescriptor[Supervisor]] = Set(Terminate)
      override val events: Set[EventDescriptor[Supervisor]] = Set(SupervisorTerminated)
      override val dependencies: Set[ServiceClass[_]] = Set()
    }

    object Supervisor extends Supervisor

    trait SupervisorService[C <: Supervisor] extends Service[C]

    package object control {

      val SupervisorControllerClassName = "ck::services:supervisor:control"
      val SupervisorControllerClassId: ClassId[SupervisorControl] =
        ClassId[SupervisorControl](UUID.fromString(SupervisorControllerClassName), SupervisorProtocolVersion)

    }

    package control {
      // Context Service Definition: SupervisorControl
      //
      // subscribes to the event-stream of all supervisors, and relays commands
      // and events to and from application-level services
      //
      trait SupervisorControl extends ServiceClass[SupervisorControl] {

        import commands._

        override val classId: ClassId[SupervisorControl] = SupervisorControllerClassId
        override val parent: ClassId[_] = RootServiceClass.classId
        override val commands: Set[CommandDescriptor[SupervisorControl]] = Set()
        override val events: Set[EventDescriptor[SupervisorControl]] = Set()
        override val dependencies: Set[ServiceClass[_]] = Set()
      }

      object SupervisorControl extends SupervisorControl

    }

  }

}
