package de.holisticon.ranked.model

import de.holisticon.ranked.api.model.Player
import java.util.{List => JavaList}
import scala.collection.JavaConverters._
import javax.ejb.{LocalBean, Stateless}

/**
 * Provides basic DAO functionality for accessing player resource.
 * @author Daniel Wegener
 * @author Simon Zambrovski
 *
 */
@Stateless
@LocalBean
class Dao extends GenericDao[Player] {

  def byName(name: String) : List[Player] = {
    em.createNamedQuery("Player.byName").setParameter("name",name).getResultList.asInstanceOf[JavaList[Player]].asScala.toList
  }

  /**
   * Provides entity class name
   * @return entity class
   */
  def getEntityClass: Class[Player] = classOf[Player]
}

