package nz.eqs.ck

import akka.actor._

package core {
  import service._
  class AkkaServiceDriver[C <: ServiceClass[C]](implicit actorSystem: ActorSystem) extends ServiceDriver[C] {
    val actor: ActorRef = ???
    override def consume: Command[C] => Unit = actor.forward(_)
  }

}