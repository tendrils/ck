package nz.eqs.ck

import scala.collection.parallel.immutable._

import service._

package object realm {
  private[realm] type RealmIndex = ParMap[ServiceTag[_], ServiceRef[_]]
  private[realm] type SubscriberIndex = ParMap[EventDescriptor[_,_,_], Set[Listener[_,_]]]

}

package realm {

  object Realm {
    private var default: Realm = Empty

    def apply(): Realm = default

    def set(realm: Realm): Unit = default = realm
  }

  trait Realm {

    private var _subscribers: SubscriberIndex = ParMap()

    protected def masterIndex: RealmIndex

    protected def subscribers: SubscriberIndex = _subscribers

    def contextServices: Set[ProtocolId[_]] = (
      for {
        (tag, _) <- masterIndex if tag.tagType == ByClass
      } yield tag.asInstanceOf[ProtocolId[_]]
      ).asInstanceOf[Set[ProtocolId[_]]]

    // get service by type
    def apply[P: Protocol]: ServiceRef[P] = apply[P](implicitly[Protocol[_,P]].classId)

    // get service by tag
    def apply[P <: Protocol[_,P]](tag: ServiceTag[P]): ServiceRef[P] = masterIndex(tag).asInstanceOf[ServiceRef[P]]

    def event[P <: Protocol[_,P], E <: Event[_,P,E]](e: Event[_,P,E]): Unit = subscribers(e.descriptor) foreach(f => f(e))

    def send[P <: Protocol[_,P], C <: Command[_,P,_]](c: Command[_,P,C]): Unit = send[P,C](implicitly[Protocol[_,P]].classId, c)

    def send[P <: Protocol[_,P], C <: Command[_,P,_]](id: ServiceTag[P], cmd: Command[_,P,C]): Unit

    def subscribe[P <: Protocol[_,P], E <: Event[_,P,E]](e: Event[_,P,E], f: Listener[P, E]): Unit =
      _subscribers = _subscribers + (e.descriptor -> (_subscribers(e.descriptor) + f.asInstanceOf[Listener[_, _]]))

    def unsubscribe[P <: Protocol[_,P], E <: Event[_,P,E]](e: Event[_,P,E], f: Listener[_, _]): Unit =
      _subscribers = _subscribers + (e.descriptor -> (_subscribers(e.descriptor) - f))
  }

  // a simple realm type which takes an immutable service map, and provides static references to
  // the service instances it is initialized against
  case class StaticRealm(services: RealmIndex) extends Realm {
    override val masterIndex: RealmIndex = (services map(e => () => e._2)).asInstanceOf[RealmIndex]
    override def send[P <: Protocol[_,P], C <: Command[_,P,C]](ref: ServiceRef[P], cmd: Command[_,P,C]): Unit = services(id)() send cmd
  }

  trait DelegatingRealm extends Realm {
    private var delegate: Realm = Empty
    private def alter(op: Realm => Realm): Unit = delegate = op(delegate)

    def set(delegate: Realm): Unit = alter(_ => delegate)
  }

  object Empty extends StaticRealm(ParMap())

}