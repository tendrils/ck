package nz.eqs.ck

import java.util.UUID

package object service {
  import realm._

  // TODO inject this value from boot context
  val ServiceProtocolVersion: Long = 0

  trait Command[C <: ServiceClass[C]]

  trait Event[C <: ServiceClass[C]]

  type ServiceRef[C <: ServiceClass[C]] = () => Service[C]
  type Listener[C <: ServiceClass[C], E <: Event[C]] = (Unit) => E

  implicit def callByName2StaticServiceRef[C <: ServiceClass[C]](f: => Service[C]): ServiceRef[C] = () => f

  implicit def serviceTag2ContextRef[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = implicitly[Realm[_]].apply(tag)
}

package service {

  import scala.ref.Reference

  sealed trait ServiceTag[C <: ServiceClass[C]] extends Serializable {
    // underlying service coordination protocol version which produced this tag
    val tagVersion: Long = ServiceProtocolVersion
    val tagType: TagType
    val classId: UUID         // UUID corresponding to service class-name
    val classVersion: Long    // sequential numeric service protocol version
    val instanceId: UUID      // random ID for instance tags, or "NULL" for class tags
  }

  case class ServiceId[C <: ServiceClass[C]](override val classId: String, override val classVersion: Long, override val instanceId: UUID) extends ServiceTag[C] {
    override val tagType: TagType = ByInstance
  }

  case class ClassId[C <: ServiceClass[C]](override val classId: String, override val classVersion: Long) extends ServiceTag[C] {
    override val tagType: TagType = ByClass
    override val instanceId: String = "NULL"
  }

  sealed case class TagType(value: String)

  case object ByInstance extends TagType("instance")

  case object ByClass extends TagType("class")


  trait Service[C <: ServiceClass[C]] {
    val id: ServiceId[C]
    val classId: ClassId[C]

    val descriptor: ServiceClass[C]

    def send(c: Command[C])

    def subscribe(l: Listener[C, Event[C]])

  }

  trait ServiceClass[C <: ServiceClass[C]] {
    val classId: ClassId[C]
    val commands: Set[Command[C]]
    val events: Set[Event[C]]
    val dependencies: Set[ServiceClass[_]]

  }

  trait ServiceDriver {
    def consume: (Unit) => Command[_]
  }

  trait ServiceReference[C <: ServiceClass[C]] extends Reference[Service[C]]

  object ServiceReference {

    // to be mixed with traits for local and remote access mechanism
    abstract class ReferenceByClass[C <: ServiceClass[C]](val classId: ClassId[C]) extends ServiceReference[C]

    abstract class ReferenceById[C <: ServiceClass[C]](val id: ServiceId[C]) extends ServiceReference[C]

    trait LocalReference[C <: ServiceClass[C]] extends ServiceReference[C]

    trait RemoteReference[C <: ServiceClass[C]] extends ServiceReference[C]

    trait DynamicReference[C <: ServiceClass[C]] extends ServiceReference[C]

  }

}
