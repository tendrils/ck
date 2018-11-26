package nz.eqs.ck

import java.util.UUID
import scala.ref.Reference
import scala.reflect.ClassTag

import meta._
import data._
import realm._

package object service {
  type ServiceRef[P <: Protocol[_,P]] = () => Service[_,P,_]
  type Listener[P <: Protocol[_,P], E <: EventDescriptor[_,P,E]] = E => Unit

  implicit val defaultRealm: Realm = Realm()

  implicit def callByName2StaticServiceRef[P <: Protocol[_,P]](f: => Service[_,P,_]): ServiceRef[P] = () => f

  implicit def serviceTag2ContextServiceRef[P <: Protocol[_,P]](tag: ServiceTag[P]): ServiceRef[P] = implicitly[Realm].apply(tag)

  implicit def classId2ServiceClass[P <: Protocol[_,P]](tag: ProtocolId[P]): Protocol[_,P] = implicitly[Realm].apply(tag)().descriptor
}

package service {
  import root._

  sealed trait ServiceTag[P <: Protocol[_,P]] extends Serializable {
    // underlying service coordination protocol version which produced this tag
    val tagVersion: ProtocolVersion[RootProtocol]
    val tagType: TagType
    val id: UUID
    val classId: UUID                     // UUID corresponding to service class-name
    val classVersion: ProtocolVersion[P]  // sequential numeric service protocol version
    val instanceId: UUID                  // random ID for instance tags, or "CLASS" for class tags

    def equals(tag: ServiceTag[_]): Boolean = id == tag.id
  }

  case class ServiceId[P <: Protocol[_,P]](override val classId: UUID, override val instanceId: UUID)
                                        (implicit override val tagVersion: ProtocolVersion[RootProtocol], implicit val classVersion: ProtocolVersion[P])
    extends ServiceTag[P]
  {
    override val tagType: TagType = ByInstance
    override val id: UUID = instanceId
  }

  case class ProtocolId[P <: Protocol[_,P]](override val classId: UUID)
                                         (implicit override val tagVersion: ProtocolVersion[RootProtocol], implicit override val classVersion: ProtocolVersion[P])
    extends ServiceTag[P]
  {
    override val tagType: TagType = ByClass
    override val id: UUID = classId
    override val instanceId: UUID = UUID.fromString("CLASS")
  }

  sealed case class TagType(value: String)

  case object ByInstance extends TagType("instance")

  case object ByClass extends TagType("class")

  trait Service[D <: SemanticDomain[D], P <: Protocol[D,P], S <: Service[D,P,S]] {
    val id: ServiceId[P]
    val classId: ProtocolId[P]

    val descriptor: Protocol[_,P]

    def send(c: CommandDescriptor[_,P,_])

    def subscribe(l: Listener[P, EventDescriptor[_,P,_]])

  }

  object Protocol {
    private var classes: Set[Protocol[_,_]] = Set()
    private def modify(op: (Set[Protocol[_,_]] => Set[Protocol[_,_]])): Unit = {
      classes = op(classes)
    }

    def register[D <:SemanticDomain[D], SC <: Protocol[D,SC]](serviceClass: Protocol[D,SC]): Unit = modify(_ + serviceClass)
    def registry: Set[Protocol[_,_]] = classes
    def apply[D <: SemanticDomain[D], SC <: Protocol[D,SC]: ProtocolVersion](tag: ServiceTag[SC]): Option[Protocol[D,SC]] = {
      // I think this syntax may be more expressive than a monad pipeline?
      for {
        c <- classes find(_.classId == tag)
      } yield c.asInstanceOf[Protocol[D,SC]]
    }
  }

  trait Protocol[D <: SemanticDomain[D], P <: Protocol[D,P]] {
    val version: ProtocolVersion[P]
    val classId: ProtocolId[P]
    val parent: ProtocolId[_]
    val commands: Set[CommandDescriptor[D,P,_]]
    val events: Set[EventDescriptor[D,P,_]]
    val dependencies: Set[Protocol[_,_]]

  }

  case class ProtocolVersion[P <: Protocol[_,P]]
  (protocolName: String, override val seq: Long, override val name: String)
  (implicit rpv: ProtocolVersion[RootProtocol])
    extends Version[P](seq, name)

  package object root {

    val RootProtocolName: String = "ck::protocol:root"
    val RootProtocolVersionNumber = 0
    val RootProtocolVersionName = "0.0.1"
    implicit val RootProtocolVersion: ProtocolVersion[RootProtocol] =
      ProtocolVersion[RootProtocol](RootProtocolName, RootProtocolVersionNumber, RootProtocolVersionName)(RootProtocolVersion)
    implicit val RootProtocolId: ProtocolId[RootProtocol] = ProtocolId[RootProtocol](UUID.fromString(RootProtocolName))(RootProtocolVersion)
  }

  package root {

    case class RootSemanticDomain extends SemanticDomain[RootSemanticDomain] {
      override val references: Set[SemanticDomain[_]] = Set[SemanticDomain[_]]()
      override val base: SemanticDomain[_] = _
    }



    case class RootProtocol(implicit version: ProtocolVersion[RootProtocolVersion, RootProtocol]) extends Protocol[RootProtocolVersion,RootProtocol] {
      override val classId: ProtocolId[RootProtocol] = RootProtocolId
      override val parent: ProtocolId[RootProtocol] = classId
      override val commands: Set[CommandDescriptor[RootSemanticDomain,RootProtocol,_]] = Set()
      override val events: Set[EventDescriptor[RootProtocol]] = Set()
      override val dependencies: Set[Protocol[_]] = Set()
    }

    object RootProtocol extends RootProtocol

    case class RootProtocolVersion extends ProtocolVersion[RootProtocolVersion, RootProtocol](val proto)

    object RootService {

      case class GetConcreteProtocol(sender: ServiceRef[_])
        extends Command[RootProtocol](Map("sender" -> sender)) {
        override val descriptor: CommandDescriptor[RootProtocol]
      }

      case class ReturnConcreteServiceClass(classId: ProtocolId[_]) extends EventDescriptor[RootProtocol]

    }

  }

  case class MessageDescriptor[D <: SemanticDomain[D], P <: Protocol[D,P], M <: Message[D,P,M]](params: Map[String, EntityTag[_,_]]) {
    def param(paramName: String): EntityTag[_,_] = params(paramName)
  }

  case class Message[D <: SemanticDomain[D], P <: Protocol[D,P], M <: Message[D,P,M]](descriptor: MessageDescriptor[D,P,M], params: Map[String, Serializable]) {
    def param(paramName: String): Serializable = params(paramName)
  }

  case class CommandDescriptor[D <: SemanticDomain[D], P <: Protocol[D,P], C <: Command[D,P,C]]
                                (p: Map[String, EntityTag[_,_]]) extends MessageDescriptor[D,P,C](p)

  case class EventDescriptor[D <: SemanticDomain[D], P <: Protocol[D,P], E <: Event[D,P,E]]
                                (p: Map[String, EntityTag[_,_]]) extends MessageDescriptor[D,P,E](p)

  case class Command[D <: SemanticDomain[D], P <: Protocol[D,P], C <: Command[D,P,C]]
    (override val descriptor: MessageDescriptor[D,P,C], override val params: Map[String, Entity[_,_]])
    extends Message[D,P,C](descriptor, params)

  case class Event[D <: SemanticDomain[D], P <: Protocol[D,P], E <: Event[D,P,E]]
    (override val descriptor: EventDescriptor[D,P,E], override val params: Map[String, Serializable])
    extends Message[D,P,E](descriptor, params)

  trait ServiceDriver[D <: SemanticDomain[D], P <: Protocol[D,P]] {
    def consume[M <: MessageDescriptor[D,P,M]](msg: Message[D,P,M]): Unit
  }

  trait ServiceReference[D <: SemanticDomain[D], P <: Protocol[D,P]] extends Reference[Service[P]]

  object ServiceReference {

    def apply[D <: SemanticDomain[D], P <: Protocol[D,P]]: ServiceReference[D,P] = {
      return
    }

    // to be mixed with traits for local and remote access mechanism
    trait ReferenceByClass[C <: Protocol[_]] extends ServiceReference[C] {
      def classId: ProtocolId[C]
    }

    trait ReferenceById[P <: Protocol[_,P]] extends ServiceReference[_,P] {
      def id: ServiceId[P]
    }

    trait LocalReference[P <: Protocol[_,P]]

    case class LocalReferenceById[P <: Protocol[_,P]] extends ReferenceByClass[P] with LocalReference[P]

  }

}
