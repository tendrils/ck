package nz.eqs.ck

import scala.collection.parallel.immutable.ParSeq
import scala.concurrent.ExecutionContextExecutor

package core {
  import service._

  object DefaultServiceDriver {
    trait
  }

  class DefaultServiceDriver[C <: ServiceClass[C]](implicit var executor: ExecutionContextExecutor) extends ServiceDriver[C] {
    import service._

    val incoming: ParSeq[Command[C,_]] = ParSeq()

    override def consume: Command[C,_] => Unit = ???
  }

}