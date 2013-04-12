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
  private var playerDao: PlayerDao = _

  def create(payload: Player) {
    playerDao.create(payload)
  }

  def get(): List[Player] = {
    playerDao.all
  }
}

@Stateless
class DisciplineResourceImpl extends DisciplineResource {

  @EJB
  private var disciplineDao: DisciplineDao = _

  def get(): List[Discipline] = {
    disciplineDao.all
  }
}
