package service

import nz.eqs.ck.service.{ClassId, Service, ServiceClass}

trait SupervisorServiceClass[+C <: SupervisorServiceClass[C]] extends ServiceClass[SupervisorServiceClass[C]]

  trait SupervisorService[C <: SupervisorServiceClass[C]] extends Service[C] {

  }

  case object RootSupervisorServiceClass extends SupervisorServiceClass[RootSupervisorServiceClass.type] {
    override val classId: ClassId[SupervisorServiceClass[RootSupervisorServiceClass.type]] = _
    override val commands: Set[Command[SupervisorServiceClass[RootSupervisorServiceClass.type]]] = _
    override val events: Set[Event[SupervisorServiceClass[RootSupervisorServiceClass.type]]] = _
    override val dependencies: Set[ServiceClass[_]] = _
  }