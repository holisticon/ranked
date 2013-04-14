/**
 * Data Access Object Layer.
 * @author Daniel Wegener
 * @author Simon Zambrovski
 */
package de.holisticon.ranked.model

import de.holisticon.ranked.api.model._
import de.holisticon.ranked.api.model.Player
import java.util.{List => JavaList}
import scala.collection.JavaConverters._
import javax.ejb.{LocalBean, Stateless}

/**
 * Provides basic DAO functionality for accessing discipline.
 */
@Stateless
@LocalBean
class DisciplineDao extends GenericDao[Discipline] {
  def byName(name: String) : Option[Discipline] = {
    em.createNamedQuery("Discipline.byName").setParameter("name",name).getResultList.asInstanceOf[JavaList[Discipline]].asScala.headOption
  }
}

@Stateless
@LocalBean
class RankingDao extends GenericDaoForComposite[Ranking]

@Stateless
@LocalBean
class MatchDao extends GenericDao[Match]

/**
 * Provides basic DAO functionality for accessing player.
 */
@Stateless
@LocalBean
class PlayerDao extends GenericDao[Player] {

  def byName(name: String) : Option[Player] = {
    em.createNamedQuery("Player.byName").setParameter("name",name).getResultList.asInstanceOf[JavaList[Player]].asScala.headOption
  }
}

/**
 * Provides basic DAO functionality for accessing role.
 */
@Stateless
@LocalBean
class RoleDao extends GenericDao[Role]


/**
 * Provides basic DAO functionality for accessing team resource.
 */
@Stateless
@LocalBean
class TeamDao extends GenericDao[Team] {

  def byName(name: String) : List[Team] = {
    em.createNamedQuery("Team.byName").setParameter("name",name).getResultList.asInstanceOf[JavaList[Team]].asScala.toList
  }

}

/**
 * Provides basic DAO functionality for accessing tournament resource.
 */
@Stateless
@LocalBean
class TournamentDao extends GenericDao[Tournament] {

  def byName(name: String) : List[Tournament] = {
    em.createNamedQuery("Tournament.byName").setParameter("name",name).getResultList.asInstanceOf[JavaList[Tournament]].asScala.toList
  }

}
