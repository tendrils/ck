package nz.eqs.ck

package object local {

}

package local {
  import realm._
  import service._

  package object types {
    type MessageQueue[C <: ServiceClass[C]] = Seq[Command[C,_]]
    trait QueueRef[C <: ServiceClass[C]] {
      protected var queue: MessageQueue[C]
      def apply: MessageQueue[C] = queue
      def update(op: (MessageQueue[C] => MessageQueue[C])): Unit = queue = op(queue)
    }
  }

  trait LocalRealmFacade[T <: MessageTransport[ProcessLocal]] extends Realm {
    import types._
    protected var messageQueues: Map[ServiceTag[_], QueueRef[_]] = Map()

    protected def queue[C <: ServiceClass[C]](id: ServiceTag[C]): QueueRef[C] =
      (messageQueues.find(_._1 == id) getOrElse {
        val queue: QueueRef[C] = new QueueRef[C] { var queue: MessageQueue[C] = Seq[Command[C, _]]() }
        messageQueues = messageQueues + (id -> queue.asInstanceOf[QueueRef[_]])
        queue
      }).asInstanceOf[QueueRef[C]]

    override def send[C <: ServiceClass[C], D <: CommandDescriptor[C]](id: ServiceTag[C], cmd: Command[C, D]): Unit = queue(id) update(_ :+ cmd)
  }

  // a local-side implementation type, representing a back-end message consumer which
  // may be local or remote, and hides the underlying transport mechanism
  trait MessageTransport[S <: TransportScope]

  // an enumeration type denoting how far messages may travel via a given transport,
  // without exposing the details of the transport itself
  sealed case class TransportScope(level: Int)

  final case class ProcessLocal() extends TransportScope(0)
  final case class NodeLocal() extends TransportScope(1)
  final case class NetScope() extends TransportScope(2)
  final case class DomainScope() extends TransportScope(3)
  final case class InterDomainScope() extends TransportScope(4)
  final case class Global() extends TransportScope(-1)
}
