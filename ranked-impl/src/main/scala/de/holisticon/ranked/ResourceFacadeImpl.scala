package de.holisticon.ranked

import javax.ejb.{Local, EJB, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.DisciplineResource
import de.holisticon.ranked.api.model.{RankingId, Ranking, Discipline, Player}
import de.holisticon.ranked.model.{RankingDao, DisciplineDao, PlayerDao}
import javax.ws.rs.{Consumes, Produces, Path, PathParam}
import scala.Array
import javax.ws.rs.core.MediaType

@Local
@Path("/")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
trait ResourceFacade extends PlayerResource with DisciplineResource

/**
 * @author Daniel
 */
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

  override def getPlayer(id: Long) = ???


  override def getPlayers: List[Player] = {
    playerDao.all
  }


  override def getDisciplines: List[Discipline] = {
    disciplineDao.all
  }


  override def createPlayer(name: String) {

    val player = Player(name)
    val playerRankings = disciplineDao.all
      .map(discipline => (discipline, initialEloProvider.provideInitialElo(discipline)))
      .map(disciplineElo => Ranking(RankingId(player, disciplineElo._1), disciplineElo._2, disciplineElo._2))
    playerDao.create(player)
    rankingDao.create(playerRankings)
  }

  def createDiscipline(name: String, teamCount: Int, roundCount: Int) =
    disciplineDao.create(Discipline(name,teamCount,roundCount))

}

