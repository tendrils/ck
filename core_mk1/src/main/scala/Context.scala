package nz.eqs.ck

import scala.reflect.ClassTag

package object context {

  type ContextKey[T] = Either[ClassTag[T], String]

  type ContextEntry[T] = (ContextKey[T], T)

  implicit def map2Context[V](entries: Map[ContextKey[_], ContextEntry[_]]): Context = new SimpleContext(entries)

}

package context {

  object Context {
    def apply(): Context = new SimpleContext()
  }

  trait Context {
    def apply[T: ClassTag]: Option[T] = apply(implicitly[ClassTag[T]].asInstanceOf[ContextKey[T]])

    def apply[T](key: ContextKey[T]): Option[T]

    def set[T: ClassTag](value: T): Unit = set(implicitly[ClassTag[T]].asInstanceOf[ContextKey[T]] -> value)

    def set[T](kv: (ContextKey[T], T)): Unit

  }

  class SimpleContext(protected var entries: Map[ContextKey[_], ContextEntry[_]] = Map()) extends Context {
    override def apply[T](key: ContextKey[T]): Option[ContextEntry[T]] =
      entries.get(key.asInstanceOf[ContextKey[_]]).asInstanceOf[Option[ContextEntry[T]]]

    override def set[T](kv: ContextEntry[T]) = entries = entries + kv.asInstanceOf
  }

}
