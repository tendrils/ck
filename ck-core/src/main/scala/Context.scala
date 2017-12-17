package nz.eqs.ck

import scala.reflect.ClassTag

package context {

  trait Context[T_Key, T_Val] {
    def apply[T <: T_Val](key: Class[T]): Option[T]

    def apply(key: T_Key): Option[T_Val]

    def set[T <: T_Val, C: ClassTag[T]](value: T)

    def set(key: T_Key, value: T_Val)

  }

}