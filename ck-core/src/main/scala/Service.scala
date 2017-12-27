package nz.eqs.ck

import java.util.UUID
import scala.ref.Reference

import realm._

package object service {

  // TODO inject this value from boot context
  val ServiceProtocolVersion: Long = 0

  type ServiceRef[C <: ServiceClass[C]] = () => Service[C]
  type Listener[C <: ServiceClass[C], E <: Event[C]] = E => Unit

  implicit def callByName2StaticServiceRef[C <: ServiceClass[C]](f: => Service[C]): ServiceRef[C] = () => f

  implicit def serviceTag2ContextServiceRef[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = implicitly[Realm].apply(tag)

  implicit def classId2ServiceClass[C <: ServiceClass[C]](tag: ClassId[C]): ServiceClass[C] =
}

package service {

  import scala.reflect.ClassTag

  sealed trait ServiceTag[C <: ServiceClass[C]] extends Serializable {
    // underlying service coordination protocol version which produced this tag
    val tagVersion: Long = ServiceProtocolVersion
    val tagType: TagType
    val id: UUID
    val classId: UUID         // UUID corresponding to service class-name
    val classVersion: Long    // sequential numeric service protocol version
    val instanceId: UUID      // random ID for instance tags, or "NULL" for class tags

    def equals(tag: ServiceTag[_]): Boolean = id == tag.id
  }

  case class ServiceId[C <: ServiceClass[C]](override val classId: UUID, override val classVersion: Long, override val instanceId: UUID) extends ServiceTag[C] {
    override val tagType: TagType = ByInstance
    override val id: UUID = instanceId
  }

  case class ClassId[C <: ServiceClass[C]](override val classId: UUID, override val classVersion: Long) extends ServiceTag[C] {
    override val tagType: TagType = ByClass
    override val id: UUID = classId
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
    val parent: ClassId[_]
    val commands: Set[Command[C]]
    val events: Set[Event[C]]
    val dependencies: Set[ServiceClass[_]]

  }

  trait Command[C <: ServiceClass[C]] {
    def apply(paramName: String): ClassTag[_] = params(paramName)
    def params: Map[String, ClassTag[_]]
  }

  trait Event[C <: ServiceClass[C]]

  trait ServiceDriver[C <: ServiceClass[C]] {
    def consume: Command[C] => Unit
  }

  trait ServiceReference[C <: ServiceClass[C]] extends Reference[Service[C]]

  object ServiceReference {

    // to be mixed with traits for local and remote access mechanism
    trait ReferenceByClass[C <: ServiceClass[_]] extends ServiceReference[C] {
      def classId: ClassId[C]
    }

    trait ReferenceById[C <: ServiceClass[C]] extends ServiceReference[C] {
      def id: ServiceId[C]
    }

  }

}
