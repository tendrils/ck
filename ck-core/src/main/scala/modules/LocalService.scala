package nz.eqs.ck
package service

package object local {

}

package local {
  import realm._

  package object types {
    type MessageQueue[C <: ServiceClass[C]] = (ServiceTag[C], Seq[Command[C,_]])
  }

  trait LocalRealm extends Realm {
    import types._
    private var messageQueues: Set[MessageQueue[_]] = Set()

  }

}