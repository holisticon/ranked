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
class DisciplineDao extends GenericDao[Discipline] with GenericDaoForNamed[Discipline]

@Stateless
@LocalBean
class MatchDao extends GenericDao[Match]

@Stateless
@LocalBean
class ParticipationDao extends GenericDaoForComposite[Participation]

/**
 * Provides basic DAO functionality for accessing player.
 */
@Stateless
@LocalBean
class PlayerDao extends GenericDao[Player] with GenericDaoForNamed[Player]

 
@Stateless
@LocalBean
class RankingDao extends GenericDaoForComposite[Ranking]

@Stateless
@LocalBean
class PlayerResultDao extends GenericDaoForComposite[PlayerResult]

/**
 * Provides basic DAO functionality for accessing role.
 */
@Stateless
@LocalBean
class RoleDao extends GenericDao[Role] {

  def byDiscipline(discipline: Discipline) : Map[Long, Role] = {
    em.createNamedQuery("Role.byDisciplineId").setParameter("disciplineId", discipline.getId).getResultList.asInstanceOf[JavaList[Role]].asScala.map(role => (role.getId(), role)).toMap
  }
}


/**
 * Provides basic DAO functionality for accessing team resource.
 */
@Stateless
@LocalBean
class TeamDao extends GenericDao[Team] with GenericDaoForNamed[Team] {


  def byPlayerList(playerIds: List[Long]) : Team = {
    /*
     TODO: find an existing team by player ids or create a new one and return the reference.
     */
    em.createNamedQuery("Team.byPlayerIds").setParameter("playerIds", "").getSingleResult.asInstanceOf[Team]
  }
}

/**
 * Provides basic DAO functionality for accessing tournament resource.
 */
@Stateless
@LocalBean
class TournamentDao extends GenericDao[Tournament] with GenericDaoForNamed[Tournament]

