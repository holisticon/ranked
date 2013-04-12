/**
 * Data Access Object Layer.
 * @author Daniel Wegener
 * @author Simon Zambrovski
 */
package de.holisticon.ranked.model

import de.holisticon.ranked.api.model._
import javax.ejb.{LocalBean, Stateless}

/**
 * Provides basic DAO functionality for accessing discipline.
 */
@Stateless
@LocalBean
class DisciplineDao extends GenericDao[Discipline] {
  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Discipline] = classOf[Discipline]
}

@Stateless
@LocalBean
class MatchDao extends GenericDao[Match] {
  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Match] = classOf[Match]
}

/**
 * Provides basic DAO functionality for accessing player.
 */
@Stateless
@LocalBean
class PlayerDao extends GenericDao[Player] {

  def byName(name: String) : List[Player] = {
    em.createNamedQuery("Player.byName").getResultList.asInstanceOf[List[Player]]
  }

  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Player] = classOf[Player]
}

/**
 * Provides basic DAO functionality for accessing role.
 */
@Stateless
@LocalBean
class RoleDao extends GenericDao[Role] {
  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Role] = classOf[Role]
}


/**
 * Provides basic DAO functionality for accessing team resource.
 */
@Stateless
@LocalBean
class TeamDao extends GenericDao[Team] {

  def byName(name: String) : List[Team] = {
    em.createNamedQuery("Team.byName").getResultList.asInstanceOf[List[Team]]
  }

  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Team] = classOf[Team]
}

/**
 * Provides basic DAO functionality for accessing tournament resource.
 */
@Stateless
@LocalBean
class TournamentDao extends GenericDao[Tournament] {
  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Tournament] = classOf[Tournament]
}
