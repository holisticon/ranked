package de.holisticon.ranked

import de.holisticon.ranked.api.PlayerResource
import javax.ejb.{EJB, Stateless}
import de.holisticon.ranked.api.model.{RankingId, Ranking, Player}
import de.holisticon.ranked.model.{RankingDao, DisciplineDao, PlayerDao}

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

  override def getPlayer(id: Long) = playerDao.byId(id)

  override def getPlayerByName(name: String) = playerDao.byName(name)

  override def getPlayers: List[Player] = playerDao.all


  override def createPlayer(name: String) {

    val player = Player(name)
    playerDao.create(player)
    val initialRankings = disciplineDao.all
      .map(discipline => (discipline, initialEloProvider.provideInitialElo(discipline)))
      .map(disciplineElo => Ranking(RankingId(player, disciplineElo._1), disciplineElo._2, disciplineElo._2))
    rankingDao.create(initialRankings)
  }


  override def deletePlayer(id: Long) = playerDao.byId(id).map(playerDao.delete(_))

}
