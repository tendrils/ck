package nz.eqs.ck

import scala.collection.parallel.immutable._

import service._

package object realm {
  private[realm] type RealmIndex = ParMap[ServiceTag[_], ServiceRef[_]]
  private[realm] type SubscriberIndex = ParMap[EventDescriptor[_], Set[Listener[_, _]]]

}

package realm {

  object Realm {
    private var default: Realm = Empty

    def apply(): Realm = default

    def set(realm: Realm): Unit = default = realm
  }

  trait Realm {

    protected var _subscribers: SubscriberIndex = ParMap()

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
  case class StaticRealm(services: RealmIndex) extends Realm {
    override val masterIndex: RealmIndex = (services map(e => () => e._2)).asInstanceOf[RealmIndex]
    override def send[C <: ServiceClass[C], D <: CommandDescriptor[C]](id: ServiceTag[C], cmd: Command[C,D]): Unit = services(id)() send cmd
  }

  trait DelegatingRealm extends Realm {
    private var delegate: Realm = Empty
    private def alter(op: Realm => Realm): Unit = delegate = op(delegate)

    def set(delegate: Realm): Unit = alter((_) => delegate)
  }

  object Empty extends StaticRealm(ParMap())

}