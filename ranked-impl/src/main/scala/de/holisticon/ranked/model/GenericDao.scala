package de.holisticon.ranked.model

import javax.persistence.{EntityManager, PersistenceContext}

/**
 * Generic DAO operations.
 * User: Simon Zambrivski
 */
trait GenericDao {
  @PersistenceContext
  private var em:EntityManager = _;

  def byId(id: Long) = {

  }
}
