package de.holisticon.ranked

import de.holisticon.ranked.api.PlayerResource
import javax.ejb.{EJB, Stateless}
import de.holisticon.ranked.api.model.{RankingId, Ranking, Player}
import de.holisticon.ranked.model.{RankingDao, DisciplineDao, PlayerDao}
import javax.annotation.Resource
import javax.ws.rs.core.{MediaType, UriInfo, Context}
import javax.ws.rs.ext.Providers

/**
 * @author Daniel
 */
@Stateless(name = "de.holisticon.ranked.api.PlayerResource")
class PlayerResourceImpl extends PlayerResource {

  @EJB
  private var playerDao: PlayerDao = _

  @EJB
  private var disciplineDao: DisciplineDao = _

  @EJB
  private var initialEloProvider: InitialEloProvider = _

  @EJB
  private var rankingDao: RankingDao = _

  @Context
  private var uriInfo:UriInfo = _

  override def getPlayer(id: Long):Option[Player] = playerDao.byId(id)

  override def getPlayerByName(name: String) = playerDao.byName(name)

  override def getPlayers: List[Player] = playerDao.all(ResourceFacadeHelper.extractStartIndex(uriInfo),ResourceFacadeHelper.extractMaxResults(uriInfo),ResourceFacadeHelper.extractExpand(uriInfo))

  override def createPlayer(name: String) {


    val player = Player(name)
    playerDao.create(player)
    val initialRankings = disciplineDao.all()
      .map(discipline => (discipline, initialEloProvider.provideInitialElo(discipline)))
      .map(disciplineElo => Ranking(RankingId(player, disciplineElo._1), disciplineElo._2, disciplineElo._2))
    rankingDao.create(initialRankings)
  }


  override def deletePlayer(id: Long) = playerDao.byId(id).map(playerDao.delete(_))

}
