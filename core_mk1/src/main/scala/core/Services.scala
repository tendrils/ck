package nz.eqs.ck

package modules.core

import java.util.UUID

import service._
import service.root._

package services {

  package object appmanager {
    // TODO inject this
    val AppManagerProtocolVersionNumber = 0
    val AppManagerProtocolVersionName = "0.0.1"
    val AppManagerClassName: String = "ck::services:appmanager"

    implicit val AppManagerProtocolVersion: ProtocolVersion[AppManager] =
      ProtocolVersion(AppManagerClassName, AppManagerProtocolVersionNumber, AppManagerProtocolVersionName)
    implicit val AppManagerClassId: ProtocolId[AppManager] = ProtocolId(UUID.fromString(AppManagerClassName))
  }

  package appmanager {

    import framework._

    trait AppManager extends Protocol[AppManager] {
      override val version: ProtocolVersion[AppManager] = AppManagerProtocolVersion
      override val classId: ProtocolId[AppManager] = AppManagerClassId
      override val parent: ProtocolId[_] = RootProtocol.classId
      override val commands: Set[CommandDescriptor[AppManager,_]] = Set()
      override val events: Set[EventDescriptor[AppManager]] = Set()
      override val dependencies: Set[Protocol[_]] = Set()
    }

    object AppManager extends AppManager

    trait AppManagerService[A <: CKApp[A]] extends Service[AppManager] {
      // manage lifecycle from late boot onward, context system, and (indirectly) supervisor hierarchy
    }

  }

  package object supervisor {
    // TODO inject this
    val SupervisorProtocolVersionNumber = 0
    val SupervisorProtocolVersionName = "0.0.1"
    val SupervisorClassName = "ck::services:supervisor"

    implicit val SupervisorProtocolVersion: ProtocolVersion[Supervisor] = ProtocolVersion(SupervisorClassName, SupervisorProtocolVersionNumber, SupervisorProtocolVersionName)

    implicit val SupervisorClassId: ProtocolId[Supervisor] = ProtocolId[Supervisor](UUID.fromString(SupervisorClassName))
  }
  package supervisor {

    import nz.eqs.ck.services.classes._

    package commands {

      case object Terminate extends CommandDescriptor[Supervisor](Map()) {
      }

    }

    package events {

      case object SupervisorTerminated extends EventDescriptor[Supervisor]

    }

    // Service Definition: Supervisor
    //
    // encapsulates an instance of whatever entity is used to perform hierarchical
    // supervision in the underlying execution platform (e.g. akka supervisors),
    // and relay commands and events between the supervisor control interface and
    // the underlying supervisor
    //
    trait Supervisor extends Protocol[Supervisor] {

      import supervisor.commands._
      import supervisor.events._

      override val version: ProtocolVersion[Supervisor] = SupervisorProtocolVersion
      override val classId: ProtocolId[Supervisor] = SupervisorClassId
      override val parent: ProtocolId[_] = RootProtocol.classId
      override val commands: Set[CommandDescriptor[Supervisor]] = Set(Terminate)
      override val events: Set[EventDescriptor[Supervisor]] = Set(SupervisorTerminated)
      override val dependencies: Set[Protocol[_]] = Set()
    }

    object Supervisor extends Supervisor

    trait SupervisorService[C <: Supervisor] extends Service[C]

    package object control {
      val SupervisorControlProtocolVersionNumber = 0
      val SupervisorControlProtocolVersionName = "0.0.1"
      val SupervisorControlClassName = "ck::services:supervisor:control"

      implicit val SupervisorControlProtocolVersion: ProtocolVersion[SupervisorControl] =
        ProtocolVersion(SupervisorControlClassName, SupervisorControlProtocolVersionNumber, SupervisorControlProtocolVersionName)
      implicit val SupervisorControlClassId: ProtocolId[SupervisorControl] =
        ProtocolId[SupervisorControl](UUID.fromString(SupervisorControlClassName))

    }

    package control {
      // Context Service Definition: SupervisorControl
      //
      // subscribes to the event-stream of all supervisors, and relays commands
      // and events to and from application-level services
      //
      trait SupervisorControl extends Protocol[SupervisorControl] {

        override val version: ProtocolVersion[SupervisorControl] = SupervisorControlProtocolVersion
        override val classId: ProtocolId[SupervisorControl] = SupervisorControlClassId
        override val parent: ProtocolId[_] = RootProtocol.classId
        override val commands: Set[CommandDescriptor[SupervisorControl]] = Set()
        override val events: Set[EventDescriptor[SupervisorControl]] = Set()
        override val dependencies: Set[Protocol[_]] = Set()
      }

      object SupervisorControl extends SupervisorControl

    }

  }

}
