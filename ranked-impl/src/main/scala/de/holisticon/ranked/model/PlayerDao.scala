package de.holisticon.ranked.model

import javax.inject.Inject
import javax.persistence.{PersistenceContext, EntityManager}
import javax.ejb.Stateless

/**
 * @author Daniel
 */
@Stateless
class PlayerDao  {

  @PersistenceContext
  private var em:EntityManager = _;


}
