package de.holisticon.ranked

import javax.ejb._
import de.holisticon.ranked.api.{TournamentResource, PlayerResource, DisciplineResource}
import de.holisticon.ranked.api.model._
import de.holisticon.ranked.model.{TournamentDao, RankingDao, DisciplineDao, PlayerDao}
import javax.ws.rs.{Consumes, Produces, Path, PathParam}
import scala.Array
import javax.ws.rs.core.MediaType
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Ranking
import de.holisticon.ranked.api.model.RankingId
import de.holisticon.ranked.api.model.Discipline
import java.util.Date
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Ranking
import de.holisticon.ranked.api.model.RankingId
import de.holisticon.ranked.api.model.Discipline
import de.holisticon.ranked.api.model.Tournament

@Local
@Path("/")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
trait ResourceFacade extends PlayerResource with DisciplineResource with TournamentResource

/**
 * @author Daniel
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless(name = "de.holisticon.ranked.ResourceFacade")
class ResourceFacadeImpl extends ResourceFacade {

  @EJB
  private var initialEloProvider: InitialEloProvider = _

  @EJB
  private var playerDao: PlayerDao = _

  @EJB
  private var disciplineDao: DisciplineDao = _

  @EJB
  private var rankingDao: RankingDao = _

  @EJB
  private var tournamentDao:TournamentDao = _

  override def getPlayer(id: Long) = playerDao.byId(id)

  override def getPlayerByName(name: String) = playerDao.byName(name)

  override def getPlayers: List[Player] =  playerDao.all

  override def getDisciplines: List[Discipline] =  disciplineDao.all


  override def createPlayer(name: String) {

    val player = Player(name)
    playerDao.create(player)

    val initialRankings = disciplineDao.all
      .map(discipline => (discipline, initialEloProvider.provideInitialElo(discipline)))
      .map(disciplineElo => Ranking(RankingId(player, disciplineElo._1), disciplineElo._2, disciplineElo._2))

    rankingDao.create(initialRankings)
  }

  override def createDiscipline(name: String, teamCount: Int, roundCount: Int) = {
    val discipline = disciplineDao.create(Discipline(name,teamCount,roundCount))
    val initialElo = initialEloProvider.provideInitialElo(discipline)
    val initialRankings = playerDao.all().map(player=>Ranking(RankingId(player,discipline),initialElo,initialElo))
    rankingDao.create(initialRankings)
  }


  override def getTournaments: List[Tournament] = tournamentDao.all()


  private def prefetchTournament(tournament:Tournament) {
    tournament.matches.size
  }

  override def getTournament(id: Long): Option[Tournament] = {
    val tournament = tournamentDao.byId(id)
    tournament map prefetchTournament
    return tournament
  }

  override def getTournamentByName(name: String): List[Tournament] = {
    val tournament = tournamentDao.byName(name)
    tournament map prefetchTournament
    return tournament
  }


  override def createTournament(disciplineId:Long, name: String, start: Long, end: Long) = {
    val discipline = disciplineDao.byId(disciplineId)
    val startDate = new Date(start)
    val endDate = new Date(end)
    tournamentDao.create(Tournament(discipline.get,name,startDate,endDate))
  }

  def getDisciplineById(id: Long): Option[Discipline] = disciplineDao.byId(id)

  def getDisciplineByName(name: String): Option[Discipline] = disciplineDao.byName(name)


  def deletePlayer(id: Long) = playerDao.byId(id).map(playerDao.delete(_))
}

