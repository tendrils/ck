package nz.eqs.ck.core

import ck.CKApp
import ck.service.Service
import nz.eqs.ck.Lifecycle

class LifecycleManager(val app: CKApp[_])
  extends Service[LifecycleManager]
    with DefaultServiceDriver[LifecycleManager] {
  import ck.Lifecycle._
  def phases(): Seq[Main.Phase] = {

  }
}

object LifecycleManager {

}