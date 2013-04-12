package de.holisticon.ranked

import javax.ejb.{LocalBean, EJB, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.DisciplineResource
import de.holisticon.ranked.api.model.{Discipline, Player}
import de.holisticon.ranked.model.{DisciplineDao, PlayerDao}
import javax.ws.rs.PathParam


/**
 * @author Daniel
 */
@LocalBean
@Stateless
class PlayerResourceImpl extends PlayerResource with DisciplineResource {

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

