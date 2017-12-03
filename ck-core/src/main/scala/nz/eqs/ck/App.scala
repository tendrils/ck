package nz.eqs.ck

abstract class App[A: App](services: List[AppSpecificService[A,A,_]]) extends AppSpecificModule[A,A](services) {

}
