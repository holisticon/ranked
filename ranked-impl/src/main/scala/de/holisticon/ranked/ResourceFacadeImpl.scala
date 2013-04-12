package de.holisticon.ranked

import javax.ejb.{Local, EJB, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.DisciplineResource
import de.holisticon.ranked.api.model.{Discipline, Player}
import de.holisticon.ranked.model.{DisciplineDao, PlayerDao}
import javax.ws.rs.{Consumes, Produces, Path, PathParam}
import scala.Array
import javax.ws.rs.core.MediaType

@Local
@Path("/")
@Produces( Array ( MediaType.APPLICATION_JSON ))
@Consumes( Array ( MediaType.APPLICATION_JSON ))
trait ResourceFacade extends PlayerResource with DisciplineResource

/**
 * @author Daniel
 */
@Stateless(name="de.holisticon.ranked.ResourceFacade")
class ResourceFacadeImpl extends ResourceFacade {

  @EJB
  private var playerDao: PlayerDao = _

  @EJB
  private var disciplineDao: DisciplineDao = _

  def getPlayer(@PathParam("id") id: Long) = ???

  override def createPlayer(payload: Player) = {
    playerDao.create(payload)
  }

  override def getPlayers: List[Player] = {
    playerDao.all
  }


  override def getDisciplines: List[Discipline] = {
    disciplineDao.all
  }
}

