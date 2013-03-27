package de.holisticon.ranked.api.model

import javax.persistence.{GenerationType, GeneratedValue, Id, MappedSuperclass}
import annotation.meta.field

/**
 * @author Daniel
 */
@MappedSuperclass
abstract class EntityBase(@(Id @field) @(GeneratedValue @field)(strategy = GenerationType.AUTO) id:Long)
