package nz.eqs.ck.core

import nz.eqs.ck.{App, AppSpecificService}

abstract class CKApplicationService[A: App[A]] extends AppSpecificService[A,A,CKApplicationService[A]] {

}
