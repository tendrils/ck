package nz.eqs.ck

package modules.core {
  import akka.actor._

  import framework._
  import module._
  import realm._
  import service._
  import service.root._
  import services.appmanager._
  import services.supervisor.control._

  trait CoreModule extends Module[CoreModule] {

    override val requires: Set[Module[_]] = Set()

    override val provides: Set[Protocol[_]] = Set(AppManager, SupervisorControl)
    override def init: Unit = {
    }
    override def load(env: Realm): Unit = {
      val system: ActorSystem = ActorSystem.apply()
    }
  }

  object CoreModule extends CoreModule {
    Module.register(this)
    init
  }

  abstract class AkkaServiceRoot[C <: Protocol[C]](name: String) extends Service[C] with Actor {
    override def receive: Receive = {
      case Message[MessageDescriptor[RootProtocol]] (desc: MessageDescriptor[RootProtocol], params: Map[String,Serializable]) =>
    }
  }

  // encapsulates the actor system which supports this service tree
  class AkkaAppManager[A <: CKApp[A]](system: ActorSystem) extends AkkaService[AppManager](AppManagerClassName) {

  }

}