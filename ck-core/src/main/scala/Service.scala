package ck

import java.util.UUID

package object service {

  trait Command[+C <: ServiceClass[C]]

  trait Event[+C <: ServiceClass[C]]

  type ServiceId[C <: ServiceClass[C]] = UUID
  type ClassId[C <: ServiceClass[C]] = UUID
  type ServiceRef[+C <: ServiceClass[C]] = () => Service[C]
  type Listener[-E <: Event[ServiceClass[_]]] = (E => ())

  implicit def callByName2StaticServiceRef[C <: ServiceClass[C]](f: => Service[C]): ServiceRef[C] = () => f
}

package service {

  import scala.ref.Reference

  trait Service[+C <: ServiceClass[C]] {

    val id: ServiceId[C]
    val classId: ClassId[C]

    val descriptor: ServiceClass[C]

    def send(c: Command[C])

    def subscribe(l: Listener[Event[C]])

  }

  trait ServiceClass[+C <: ServiceClass[C]] {

    val classId: ClassId[C]
    val commands: Set[Command[C]]
    val events: Set[Event[C]]
    val dependencies: Set[ServiceClass[_]]

  }

  trait ServiceDriver {
    def consume: (Command[_]) => ()
  }

  trait ServiceReference[C <: ServiceClass[C]] extends Reference[Service[C]]

  object ServiceReference {

    // to be mixed with traits for local and remote access mechanism
    abstract class ReferenceByClass[C <: ServiceClass[C]](val classId: ClassId[C]) extends ServiceReference[C]

    abstract class ReferenceById[C <: ServiceClass[C]](val id: ServiceId[C]) extends ServiceReference[C]

    trait LocalReference[C <: ServiceClass[C]] extends ServiceReference[C]

    trait RemoteReference[C <: ServiceClass[C]] extends ServiceReference[C]

    trait DynamicReference[C <: ServiceClass[C]] extends ServiceReference[C]

  }

}
