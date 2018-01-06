package nz.eqs.ck

package modules.core {
  import akka.actor._

  import app._
  import module._
  import realm._
  import service._
  import services.appmanager._
  import services.supervisor.control._

  trait Core extends Module[Core] {

    override val dependencies: Set[Module[_]] = Set()
    override val provides: Set[ServiceClass[_]] = Set(AppManager, SupervisorControl)
    override def init: Unit = {
    }
    override def load(env: Realm): Unit = {
      val system: ActorSystem = ActorSystem.apply()
    }
  }

  object Core extends Core {
    Module.register(this)
    init
  }

  abstract class AkkaService[C <: ServiceClass[C]](name: String, system: ActorSystem) extends Service[C] {
    val actor: ActorRef = system.actorOf(Props.default, name)

    override def subscribe(l: Listener[C, EventDescriptor[C]]): Unit = actor.
  }

  // encapsulates the actor system which supports this service tree
  class AkkaAppManager[A <: CKApp[A]](system: ActorSystem) extends AkkaService[AppManager](AppManagerClassName, system) {

  }

}