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
      import services.supervisor.controller._

      override val classId: ClassId[RootServiceClass] = ClassId(UUID.fromString(RootServiceClassName), ServiceProtocolVersion)
      override val parent: ClassId[RootServiceClass] = classId
      override val commands: Set[CommandDescriptor[RootServiceClass]] = Set()
      override val events: Set[EventDescriptor[RootServiceClass]] = Set()
      override val dependencies: Set[ServiceClass[_]] = Set(AppManager, SupervisorControllerClass)
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

    val SupervisorClassId: ClassId[SupervisorClass] = ClassId[SupervisorClass](UUID.fromString(SupervisorClassName), SupervisorProtocolVersion)
  }
  package supervisor {

    import appmanager._
    import services.classes._

    package commands {

      import scala.reflect.ClassTag

      case object Terminate extends CommandDescriptor[SupervisorClass] {
        override def params: Map[String, ClassTag[_]] = ???
      }

    }

    package events {
      import scala.reflect.ClassTag

      case object SupervisorTerminated extends EventDescriptor[SupervisorClass] {
        override val params: Map[String, ClassTag[_]] = Map("supervisorId" -> ClassTag[ServiceId[_]], "exitStatus" -> ClassTag[String]).asInstanceOf[Map[String,ClassTag[_]]]
      }

    }

    trait SupervisorClass extends ServiceClass[SupervisorClass] {

      import supervisor.commands._
      import supervisor.events._

      override val classId: ClassId[SupervisorClass] = SupervisorClassId
      override val parent: ClassId[_] = RootServiceClass.classId
      override val commands: Set[CommandDescriptor[SupervisorClass]] = Set(Terminate)
      override val events: Set[EventDescriptor[SupervisorClass]] = Set(SupervisorTerminated)
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
        override val commands: Set[CommandDescriptor[SupervisorControllerClass]] = Set()
        override val events: Set[EventDescriptor[SupervisorControllerClass]] = Set()
        override val dependencies: Set[ServiceClass[_]] = Set()
      }

      object SupervisorControllerClass extends SupervisorControllerClass

    }

  }

}
