package nz.eqs.ck

import service._

package object realm {
  private[realm] type RealmIndex = Map[ServiceTag[_], ServiceRef[_]]
  private[realm] type SubscriberIndex = Map[EventDescriptor[_], Set[Listener[_, _]]]

}

package realm {

  object Realm {
    private var default: Realm = Empty

    def apply(): Realm = default

    def set(realm: Realm): Unit = default = realm
  }

  trait Realm {

    protected var _subscribers: SubscriberIndex = Map()

    protected def masterIndex: RealmIndex

    protected def subscribers: SubscriberIndex = _subscribers

    def contextServices: Set[ClassId[_]] = (
      for {
        (tag, _) <- masterIndex if tag.tagType == ByClass
      } yield tag.asInstanceOf[ClassId[_]]
      ).asInstanceOf[Set[ClassId[_]]]

    // get service by type
    def apply[C: ServiceClass]: ServiceRef[C] = apply[C](implicitly[ServiceClass[C]].classId)

    // get service by tag
    def apply[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = masterIndex(tag).asInstanceOf[ServiceRef[C]]

    def event[C : ServiceClass[C], D <: EventDescriptor[C]](e: Event[C,D]): Unit = subscribers(e.descriptor) foreach(f => f(e))

    def send[C: ServiceClass, D <: CommandDescriptor[C]](c: Command[C,D]): Unit = send[C,D](implicitly[ServiceClass[C]].classId, c)

    def send[C <: ServiceClass[C], D <: CommandDescriptor[C]](id: ServiceTag[C], cmd: Command[C,D]): Unit

    def subscribe[C <: ServiceClass[C], E <: EventDescriptor[C]](e: E, f: Listener[C, E]): Unit =
      _subscribers = _subscribers + (e -> (_subscribers(e) + f.asInstanceOf[Listener[_, _]]))

    def unsubscribe[C <: ServiceClass[C], E <: EventDescriptor[C]](e: E, f: Listener[_, _]): Unit =
      _subscribers = _subscribers + (e -> (_subscribers(e) - f))
  }

  // a simple realm type which takes an immutable service map, and provides static references to
  // the service instances it is initialized against
  abstract class StaticRealm(val services: Map[ServiceTag[_], Service[_]]) extends Realm {
    override val masterIndex: RealmIndex = (services map(e => () => e._2)).asInstanceOf[RealmIndex]
    override def send[C <: ServiceClass[C], D <: CommandDescriptor[C]](id: ServiceTag[C], cmd: Command[C,D]): Unit = services(id) send cmd
  }

  trait DelegatingRealm extends Realm {
    private var delegate: Realm = Empty

    def setDelegate(delegate: Realm): Unit = this.delegate = delegate
  }

  object Empty extends StaticRealm(Map())

}