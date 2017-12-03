package nz.eqs.ck

abstract class Module[M: Module[M]](val services: List[Service[M,_]]) {

}

abstract class AppSpecificModule[A: App, M: AppSpecificModule[A,M]](val appServices: List[AppSpecificService[A,M,_]]) extends Module[AppSpecificModule[A,M]](appServices)