package ck.api
package ck.service

trait SupervisorServiceClass[+C <: SupervisorServiceClass[C]] extends ServiceClass[SupervisorServiceClass[C]]

trait SupervisorService[C <: SupervisorServiceClass[C]] extends Service[C] {

}

object SupervisorServiceClass {

}

sealed trait RootSupervisorService extends SupervisorService[RootSupervisorService] {

}

sealed trait RootSupervisorServiceClass extends SupervisorServiceClass[RootSupervisorServiceClass]

case object RootSupervisorServiceClass extends RootSupervisorServiceClass