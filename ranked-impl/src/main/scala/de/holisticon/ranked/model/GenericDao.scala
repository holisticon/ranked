package de.holisticon.ranked.model

import javax.persistence.{EntityManager, PersistenceContext}
import scala.Predef._
import scala.collection.JavaConverters._
import de.holisticon.ranked.api.model.PersistentEntity

/**
 * Generic DAO operations.
 * User: Simon Zambrivski
 * @tparam T type of entity.
 */
trait GenericDao[T <: PersistentEntity] {
  @PersistenceContext
  var em:EntityManager = _

  /**
   * Finder for retrieving entities by id.
   * @param id id of entity
   * @return entity
   */
  def byId(id: Long) : T = {
     em.find(getEntityClass, id)
  }

  /**
   * Finder to get all entities of a type
   * @return list of entities
   */
  def all() : List[T] = {
    em.createNamedQuery(getAllFinderName).getResultList.asInstanceOf[java.util.List[T]].asScala.toList
  }

  /**
   * Saves an entity to DB
   * @param payload entity to save
   * @return saved entity.
   */
  def create(payload: T) : T = {
    em.persist(payload)
    payload
  }

  /**
   * Provides entity class name
   * @return entity class
   */
  // def getEntityClass(implicit manifest:Manifest[T]) : Class[T] = manifest.runtimeClass.asInstanceOf[Class[T]]
  def getEntityClass : Class[T]
  /**
   * Default finder name.
   * @return name of the finder.
   */
  def getAllFinderName = {
    getEntityClass.getSimpleName + ".all"
  }
}
