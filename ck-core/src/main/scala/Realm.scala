package nz.eqs.ck

package object realm {

}

package realm {
  import java.util.UUID

  import service._
  import context._

  import scala.reflect.ClassTag

  trait RealmContext extends Context[ServiceRef[_]]

  trait Realm {
    protected var servicesByClass: Map[ClassId[_],ServiceRef[_]] = Map()

    def contextServices: Set[ClassId[_]] = servicesByClass.keySet

    // get service by type
    def apply[C: ServiceClass]: ServiceRef[C] = apply[C](implicitly[ServiceClass[C]].classId)
    // get service by tag
    def apply[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = {
      match tag {
        case ServiceId[C](_, _, instanceId: UUID) => id
      }
    }

    def send[C <: ServiceClass[C]]: Command[C] => Unit
    def send[C <: ServiceClass[C]](id: ServiceTag[C]): Command[C] => Unit

    def subscribe[C: ServiceClass, M <: Event[ServiceClass[C]]](f: Listener[C,M])
    def unsubscribe[C: ServiceClass, M <: Event[ServiceClass[C]]](f: Listener[C,M])
  }

  class SimpleRealm(val values: Map[ServiceTag[_],ServiceRef[_]]) extends Realm {
    override def contextServices: Set[ClassId[_]] = ???

    override def apply[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = ???

    override def send[C <: ServiceClass[C]]: Command[C] => Unit = ???

    override def send[C <: ServiceClass[C]](id: ServiceTag[C]): Command[C] => Unit = ???

    override def subscribe[C: ServiceClass, M <: Event[ServiceClass[C]]](f: Listener[C, M]): Unit = ???

    override def unsubscribe[C: ServiceClass, M <: Event[ServiceClass[C]]](f: Listener[C, M]): Unit = ???

    override def apply[T <: ServiceRef[_]](implicit tag: ClassTag[T]): Option[T] = ???

    override def apply[T <: ServiceTag[_]](key: ContextKey[T]): Option[T] = ???

    override def set(kv: (ContextKey[ServiceTag[_]], ServiceTag[_])): Unit = ???
  }

  trait DelegatingRealm extends Realm {
    private var delegate: Realm = Empty
    def setDelegate(delegate: Realm) = this.delegate = delegate
  }

  object Realm extends Realm with DelegatingRealm
  object Empty extends Realm
}