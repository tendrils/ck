package nz.eqs.ck

abstract class Service[M: Module[M], S: Service[M,S]] {
  import Service._
  def send(c: Command[M,S])
  def addListener(l: Listener[M,S])
}

object Service {
  abstract class Command[M: Module[M], S: Service[M,S]]
  abstract class Event[M: Module[M], S: Service[M,S]]

  type Listener[M: Module[M], S: Service[M,S]] = (Event[M,S] => ())
}

abstract class AppSpecificService[A: App[A], M: AppSpecificModule[A,S], S: AppSpecificService[A,M,S]] extends Service[M,S] {
  import AppSpecificService._
  def send(c: Command[A,M,S])
  def addListener(l: Listener[A,M,S])
}

object AppSpecificService {
  abstract class Command[A: App[A], M: AppSpecificModule[A,M], S: AppSpecificService[A,M,S]]
  abstract class Event[A: App[A], M: AppSpecificModule[A,M], S: AppSpecificService[A,M,S]]

  type Listener[A: App[A], M: AppSpecificModule[A,M], S: AppSpecificService[A,M,S]] = (Event[A,M,S] => ())
}