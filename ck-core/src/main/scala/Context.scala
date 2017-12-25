package nz.eqs.ck

import scala.reflect.ClassTag

package object context {

  type ContextKey[T] = Either[ClassTag[T], String]

  implicit def map2Context[V](entries: Map[ContextKey[V], V]): Context[V] = new SimpleContext(entries)

}

package context {

  object Context {
    def apply[V](): Context[V] = new SimpleContext[V]()
  }

  trait Context[V] {
    def apply[T <: V](implicit tag: ClassTag[T]): Option[T] = apply(tag.asInstanceOf[ContextKey[T]])

    def apply[T <: V](key: ContextKey[T]): Option[T]

    def set[T <: V](implicit tag: ClassTag[T], value: T) = set(tag.asInstanceOf[ContextKey[V]] -> value)

    def set(kv: (ContextKey[V], V))

  }

  class SimpleContext[V](var entries: Map[ContextKey[V], V] = Map()) extends Context[V] {
    override def apply[T <: V](key: ContextKey[T]): Option[T] = entries(key)

    override def set(kv: (ContextKey[V], V)) = entries = entries + kv
  }

}