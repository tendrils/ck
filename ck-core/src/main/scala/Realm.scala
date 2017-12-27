package nz.eqs.ck

package object realm {

}

package realm {

  import service._

  object Realm {
    private var default: Realm = Empty

    def apply(): Realm = default
  }

  trait Realm {
    private var _subscribers: Map[Event[_], Set[Listener[_, _]]] = Map()

    protected def masterIndex: Map[ServiceTag[_], ServiceRef[_]]

    protected def subscribers: Map[Event[_], Set[Listener[_, _]]] = _subscribers

    def contextServices: Set[ClassId[_]] = (
      for {
        (tag, _) <- masterIndex if tag.tagType == ByClass
      } yield tag.asInstanceOf[ClassId[_]]
      ).asInstanceOf[Set[ClassId[_]]]

    // get service by type
    def apply[C: ServiceClass]: ServiceRef[C] = apply[C](implicitly[ServiceClass[C]].classId)

    // get service by tag
    def apply[C <: ServiceClass[C]](tag: ServiceTag[C]): ServiceRef[C] = masterIndex(tag).asInstanceOf[ServiceRef[C]]

    def send[C: ServiceClass]: Command[C] => Unit = send[C](implicitly[ServiceClass[C]].classId)

    def send[C <: ServiceClass[C]](id: ServiceTag[C]): Command[C] => Unit

    def subscribe[C <: ServiceClass[C], E <: Event[C]](e: E, f: Listener[C, E]) =
      _subscribers = _subscribers + (e -> (_subscribers(e) + f.asInstanceOf[Listener[_, _]]))

    def unsubscribe[C <: ServiceClass[C], E <: Event[C]](e: Event[C], f: Listener[_, _]) =
      _subscribers = _subscribers + (e -> (_subscribers(e) - f))
  }

  abstract class StaticRealm(val masterIndex: Map[ServiceTag[_], ServiceRef[_]]) extends Realm {
    override def send[C <: ServiceClass[C]](id: ServiceTag[C]): Command[C] => Unit = ???
  }

  trait DelegatingRealm extends Realm {
    private var delegate: Realm = Empty

    def setDelegate(delegate: Realm) = this.delegate = delegate
  }

  object Empty extends StaticRealm(Map())

}