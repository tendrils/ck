package nz.eqs.ck.core

import nz.eqs.ck.ServiceDriver
import ck.service.Service

object DefaultServiceDriver {
  trait
}

trait DefaultServiceDriver[S <: Service[S]] extends ServiceDriver[S] {
  import Service._
  override def send(c: Command[S]): Unit = {

  }

}
