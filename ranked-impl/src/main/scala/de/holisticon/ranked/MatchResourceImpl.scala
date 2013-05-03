package de.holisticon.ranked

import javax.ejb._
import de.holisticon.ranked.api.MatchResource
import de.holisticon.ranked.model._
import de.holisticon.ranked.api.model._
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Discipline
import de.holisticon.ranked.api.model.MatchResult
import de.holisticon.ranked.api.model.MatchTeam
import de.holisticon.ranked.api.model.Tournament


/**
 * Provides a resource for recording match results.
 * @author Simon
 */
@Stateless(name = "de.holisticon.ranked.api.MatchResource")
class MatchResourceImpl extends MatchResource {

  val LOG = LoggerFactory.getLogger(classOf[MatchResourceImpl])


  @EJB
  private var disciplineDao: DisciplineDao = _

  @EJB
  private var tournamentDao: TournamentDao = _

  @EJB
  private var playerDao: PlayerDao = _

  @EJB
  private var teamDao: TeamDao = _

  @EJB
  private var roleDao: RoleDao = _

  @EJB
  private var playerResultDao: PlayerResultDao = _

  @EJB
  private var matchDao: MatchDao = _

  @EJB
  private var participationDao: ParticipationDao = _


  override def createMatch(matchResult: MatchResult) = {

    val discipline: Discipline = disciplineDao.byId(matchResult.disciplineId).get
    val tournament: Tournament = tournamentDao.byId(matchResult.tournamentId).get

    // 1. match (date, description, discipline, tournament)
    val savedMatch = matchDao.create(Match(matchResult.time, matchResult.description, discipline, tournament))

    // 2. foreach player -> participation(eloChange, player, match)
    // 2.1. find or create teams
    val matchTeams: List[MatchTeam] = matchResult.teams.asScala.toList
    val teams = matchTeams.map(matchTeam => (matchTeam, teamDao.byPlayerList(matchTeam.members.asScala.toList))).toMap

    // 2.2. load players
    val players: List[Player] = matchTeams.map(team => team.members.asScala).flatten.map(playerId => playerDao.byId(playerId).get).toList

    // 2.3 TODO: calculate ELO!
    val eloChange : Int = 10

    // 2.4 for each player -> save participation
    val savedParticipations = players.map(
      player => Participation(ParticipationId(player, savedMatch), eloChange)).map(
        participation => (participation.player.getId, participationDao.create(participation))).toMap

    // 3. load roles
    val roles: Map[Long, Role] = roleDao.byDiscipline(discipline)

    // 4. for each round and player in a role a playerResult
    matchResult.rounds.asScala.map(
      round => round.teamResults.asScala.map(
        result => result.playerRoles.asScala.map(
          // create player results
          role => PlayerResult(PlayerResultId(savedParticipations.get(role.playerId).get, teams.get(result.team).get), round = 1, result.score, roles.get(role.roleId).get)
        ).map(
          // persist results
          result => playerResultDao.create(result)
        )
      )
    )
  }
}
