package de.holisticon.ranked

import javax.ejb.{EJB, Local, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.model.{Dao, PlayerDao}
import scala.collection.JavaConverters._


/**
 * @author Daniel
 */
@Stateless
@Local
class PlayerResourceImpl extends PlayerResource {


  @EJB
  private var dao: Dao = _

  def create(payload: Player) {
    dao.create(payload)
  }

  def get(): List[Player] = {
    dao.all
  }



}
