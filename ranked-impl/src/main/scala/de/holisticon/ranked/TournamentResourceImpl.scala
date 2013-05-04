package de.holisticon.ranked

import javax.ejb._
import de.holisticon.ranked.api.{TournamentResource, PlayerResource, DisciplineResource}
import de.holisticon.ranked.api.model._
import de.holisticon.ranked.model.{TournamentDao, RankingDao, DisciplineDao, PlayerDao}
import javax.ws.rs.{Consumes, Produces, Path, PathParam}
import scala.Array
import javax.ws.rs.core.{UriInfo, Context, MediaType}
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
import javax.annotation.Resource
import org.slf4j.LoggerFactory


/**
 * @author Daniel
 */
@Stateless(name = "de.holisticon.ranked.api.TournamentResource")
class TournamentResourceImpl extends TournamentResource {

  val LOG = LoggerFactory.getLogger(classOf[TournamentResourceImpl])


  @EJB
  private var disciplineDao: DisciplineDao = _

  @EJB
  private var tournamentDao:TournamentDao = _

  @Context
  private var uriInfo:UriInfo = _


  override def getTournaments: List[Tournament] = tournamentDao.all()

  override def getTournament(id: Long): Option[Tournament] = tournamentDao.byId(id)

  override def getTournamentByName(name: String): Option[Tournament] = tournamentDao.byName(name)


  override def createTournament(disciplineId:Long, name: String, start: Long, end: Long) = {
    val discipline = disciplineDao.byId(disciplineId)
    val startDate = new Date(start)
    val endDate = new Date(end)
    tournamentDao.create(Tournament(discipline.get,name,startDate,endDate))
  }


}

