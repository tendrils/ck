package ck

import scala.reflect.ClassTag

package context {

  trait Context[T_Ctx] {
    def apply[T <: T_Ctx](key: Class[T]): Option[T]

    def apply(key: Symbol): Option[T_Ctx]

    def set[T <: T_Ctx, C: ClassTag[T]](value: T)

    def set(key: Symbol, value: T_Ctx)

  }

  trait ServiceContext[T_Service] extends Context[T_Service] {

  }

}