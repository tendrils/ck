package nz.eqs.ck

package data {

  trait Entity[SD <: SemanticDomain[SD], E <: Entity[SD,E]] extends Serializable

  trait EntityDescriptor[SD <: SemanticDomain[SD], E <: Entity[SD,E]] {
    val domain: SemanticDomain[SD]
  }

  trait EntityTag[SD <: SemanticDomain[SD], E <: Entity[SD,E]] extends Entity[SD,EntityTag[SD,E]]

  trait SemanticDomain[SD <: SemanticDomain[SD]] {
    val base: SemanticDomain[_]
    val references: Set[SemanticDomain[_]]
    val entities: Set[EntityDescriptor[SD,_]]
  }
}
