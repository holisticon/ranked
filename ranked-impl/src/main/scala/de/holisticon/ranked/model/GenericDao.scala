package de.holisticon.ranked.model

import javax.persistence.{EntityManager, PersistenceContext}
import scala.Predef._
import scala.collection.JavaConverters._
import de.holisticon.ranked.api.model.{Player, NamedEntity, PersistentEntity}
import javax.persistence.criteria.{Root, JoinType, FetchParent, CriteriaBuilder}
import java.util.{List => JavaList}
import scala.List

abstract class GenericDaoForComposite[T : Manifest] {
  @PersistenceContext
  var em:EntityManager = _

  final val entityClass:Class[_ <: T] = manifest[T].runtimeClass.asInstanceOf[Class[_<:T]]

  /**
   * Finder to get all entities of a type
   * @return list of entities
   */
  def all(startIndex:Int = 0, maxResults:Int = Integer.MAX_VALUE, relations:Seq[String] =Nil) : List[T] = {
    val criteriaBuilder:CriteriaBuilder = em.getCriteriaBuilder()
    val query = criteriaBuilder.createQuery(entityClass)
    val root = query.from(entityClass)
    createFetchPlanForRelations(root,relations)
    em.createQuery(query).setFirstResult(startIndex).setMaxResults(maxResults).getResultList.asScala.toList
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

  def delete(payload:T) : Unit  = em.remove(payload)

  def create(payload:Seq[T]):Seq[T] = payload.map(create)

  /**
   *
   * @param root a query root
   * @param relations a sequence of attribute paths relative to the query root.
   * @tparam N
   */
  protected def createFetchPlanForRelations[N](root:Root[N], relations:Seq[String]): Unit = {
    relations.foreach(r=>r.split(""".""").foldLeft(root.asInstanceOf[FetchParent[N,N]])((relativeRoot,segment) => relativeRoot.fetch(segment, JoinType.LEFT)))
  }

}

/**
 * Generic DAO operations.
 * User: Simon Zambrivski
 * @tparam T type of entity.
 */
trait GenericDao[T <: PersistentEntity] extends GenericDaoForComposite[T] {
  /**
   * Finder for retrieving entities by id.
   * @param id id of entity
   * @return entity
   */
  def byId(id: Long) : Option[T] = {
    Option(em.find(entityClass, id))
  }
}

trait GenericDaoForNamed[T <: NamedEntity] extends GenericDaoForComposite[T] {
  def byName(name: String, relations:Seq[String] = Nil) : Option[T] = {

    val criteriaBuilder:CriteriaBuilder = em.getCriteriaBuilder()
    val query = criteriaBuilder.createQuery(entityClass)
    val root = query.from(entityClass)
    query.where(List(criteriaBuilder.equal(root.get("name"), name)):_*)
    createFetchPlanForRelations(root,relations)
    em.createQuery(query).getResultList.asInstanceOf[JavaList[T]].asScala.headOption
  }
}