package nz.eqs.ck

package object realm {

}

package realm {

  import context._

  trait Realm[S <: Scope[S]] extends Context[ServiceTag[_],ServiceRef[_]] {
    // get service by ID
    def apply[C <: ServiceClass[C]](id: ServiceTag[C]): ServiceRef[C]

    def register[C <: ServiceClass[C]](service: Service[C])

    def send[C <: ServiceClass[C]](dest: ServiceRef[C]): (Command[C]) => ()
  }

  sealed trait Scope[S <: Scope[S]]

  object Scope {

    case object Local extends Scope[Local.type]

    case object Upstream extends Scope[Upstream.type]

    case class Dynamic[S <: Dynamic[S]]() extends Scope[Dynamic[S]]

  }

}