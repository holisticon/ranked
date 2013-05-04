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
class RankingDao extends GenericDaoForComposite[Ranking]

@Stateless
@LocalBean
class MatchDao extends GenericDao[Match]

/**
 * Provides basic DAO functionality for accessing player.
 */
@Stateless
@LocalBean
class PlayerDao extends GenericDao[Player] with GenericDaoForNamed[Player]

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
class TeamDao extends GenericDao[Team] with GenericDaoForNamed[Team]


/**
 * Provides basic DAO functionality for accessing tournament resource.
 */
@Stateless
@LocalBean
class TournamentDao extends GenericDao[Tournament] with GenericDaoForNamed[Tournament]

