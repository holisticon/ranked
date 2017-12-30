package de.holisticon.ranked.command.data

import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Access to the axon token entry table.
 */
interface TokenJpaRepository : JpaRepository<TokenEntry, TokenEntry.PK> {

  /**
   * Transform optional to kotlin nullable type.
   */
  //fun findByPk(pk: TokenEntry.PK) : TokenEntry? = findById(pk).orElse(null)

}


