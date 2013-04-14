package de.holisticon.ranked.model

import javax.persistence.{EntityManager, PersistenceContext}
import scala.Predef._
import scala.collection.JavaConverters._
import de.holisticon.ranked.api.model.PersistentEntity

abstract class GenericDaoForComposite[T : Manifest] {
  @PersistenceContext
  var em:EntityManager = _

  final val entityClass:Class[_ <: T] = manifest[T].runtimeClass.asInstanceOf[Class[_<:T]]

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
    return payload
  }

  def create(payload:Seq[T]):Seq[T] = payload.map(create)


  /**
   * Default finder name.
   * @return name of the finder.
   */
  def getAllFinderName = {
    entityClass.getSimpleName + ".all"
  }
}

/**
 * Generic DAO operations.
 * User: Simon Zambrivski
 * @tparam T type of entity.
 */
trait GenericDao[T <: PersistentEntity] extends GenericDaoForComposite[T]{
  /**
   * Finder for retrieving entities by id.
   * @param id id of entity
   * @return entity
   */
  def byId(id: Long) : Option[T] = {
    Option(em.find(entityClass, id))
  }
}
