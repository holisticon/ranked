package de.holisticon.ranked

import de.holisticon.ranked.api.DisciplineResource
import javax.ejb.{Stateless, EJB}
import de.holisticon.ranked.model.{RankingDao, PlayerDao, DisciplineDao}
import de.holisticon.ranked.api.model.{RankingId, Ranking, Discipline}
import javax.ws.rs.core.{UriInfo, Context}

/**
 * @author Daniel
 */
@Stateless(name = "de.holisticon.ranked.api.DisciplineResource")
class DisciplineResourceImpl extends DisciplineResource {

  @EJB
  private var disciplineDao: DisciplineDao = _

  @EJB
  private var initialEloProvider: InitialEloProvider = _

  @EJB
  private var playerDao: PlayerDao = _

  @EJB
  private var rankingDao: RankingDao = _

  @Context
  private var uriInfo:UriInfo = _

  override def createDiscipline(name: String, teamCount: Int, roundCount: Int) = {
    val discipline = disciplineDao.create(Discipline(name,teamCount,roundCount))
    val initialElo = initialEloProvider.provideInitialElo(discipline)
    val initialRankings = playerDao.all().map(player=>Ranking(RankingId(player,discipline),initialElo,initialElo))
    rankingDao.create(initialRankings)
  }

  override def getDisciplineById(id: Long): Option[Discipline] = disciplineDao.byId(id)
  override def getDisciplineByName(name: String): Option[Discipline] = disciplineDao.byName(name)

  override def getDisciplines: List[Discipline] =  disciplineDao.all()
}
