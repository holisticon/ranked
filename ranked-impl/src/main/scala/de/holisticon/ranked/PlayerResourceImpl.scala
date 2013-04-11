package de.holisticon.ranked

import javax.ejb.{EJB, Local, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.model.PlayerDao
import scala.collection.JavaConverters._


/**
 * @author Daniel
 */
@Stateless
@Local
class PlayerResourceImpl extends PlayerResource {


  @EJB
  private var playerDao: PlayerDao = _

  def create(payload: Player) {
    playerDao.create(payload)
  }

  def get(): List[Player] = {
    playerDao.all
  }



}
