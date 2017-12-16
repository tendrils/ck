package nz.eqs.ck
package core

import ck.service.Service

object LocalService {
  import Service._

  trait Realm extends Service.Realm {
    private var messageQueues: Map[ServiceRef[_], Seq[Command[_]]] = Map()


     def register(service: Service[_]) = synchronized({
      messageQueues = messageQueues + (service -> Seq[Command[_]]())
    })

    def send[S: Service[S]](dest: Service[S]): (Command[S]) => ()
  }

  object Realm {
    sealed trait LocalServiceReference[S <: Service[S]] {

      def apply(cmd: Command[S])
    }

    case class ReferenceByClass[S](classID: ClassId) extends LocalServiceReference[S] {

    }

  }

}