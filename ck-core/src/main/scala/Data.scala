package nz.eqs.ck

package data {

  trait Entity[D <: EntityDomain[D], E <: Entity[D,E]] extends Serializable

  trait EntityTag[D <: EntityDomain[D], E <: Entity[D,E]] extends Entity[D,EntityTag[D,E]]

  trait EntityDomain[D <: EntityDomain[D]]
}

