package nz.eqs.ck

import scala.collection.parallel.immutable.ParSeq
import scala.concurrent.ExecutionContextExecutor

package core {
  import service._

  object DefaultServiceDriver {
    trait
  }

  class DefaultServiceDriver(implicit var executor: ExecutionContextExecutor) extends ServiceDriver {
    import service._

    val incoming: ParSeq[Command[_]] = ParSeq()


    override def consume: Command[_] => Unit = ???
  }

}