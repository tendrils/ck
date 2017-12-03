package nz.eqs.ck.core

import nz.eqs.ck.{CKApplicationService, Module}

class CKCoreModule extends Module[CKCoreModule](new CKApplicationService[_] {}) {

}
