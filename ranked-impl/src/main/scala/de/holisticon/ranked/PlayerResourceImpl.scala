package de.holisticon.ranked

import javax.ejb.{LocalBean, EJB, Local, Stateless}
import de.holisticon.ranked.api.PlayerResource
import de.holisticon.ranked.api.DisciplineResource
import de.holisticon.ranked.api.model.{Discipline, Player}
import scala.collection.JavaConverters._
import de.holisticon.ranked.model.{DisciplineDao, PlayerDao}


/**
 * @author Daniel
 */
@Stateless
class PlayerResourceImpl extends PlayerResource {

  @EJB
  private var dao: PlayerDao = _

  def create(payload: Player) {
    dao.create(payload)
  }

  def get: List[Player] = {
    dao.all
  }
}

@Stateless
class DisciplineResourceImpl extends DisciplineResource {

  @EJB
  private var dao: DisciplineDao = _

  def get: List[Discipline] = {
    dao.all
  }
}
