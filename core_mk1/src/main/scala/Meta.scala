package nz.eqs.ck

package meta {
  case class Version[T](seq: Long, name: String) extends Ordered[Version[T]] {
    override def compare(that: Version[T]): Int = seq compare that.seq
  }
}
