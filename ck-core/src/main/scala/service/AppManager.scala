package nz.eqs.ck
package service

package appmanager {
  import app._

  trait AppManager[A <: CKApp[A]] {
    // manage lifecycle from late boot onward, context system, and (indirectly) supervisor hierarchy
  }

}