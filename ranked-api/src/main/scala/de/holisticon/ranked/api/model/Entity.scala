package de.holisticon.ranked.api.model

import java.util.Date

import scala.annotation.meta.field
import scala.beans.BeanProperty

import javax.persistence._
import org.codehaus.jackson.annotate.JsonProperty

/**
 * Superclass for all persistent entities.
 */
@MappedSuperclass
abstract class PersistentEntity(
                                 @BeanProperty @(Column@field)(name = "ID") @(Id@field) @(GeneratedValue@field)(strategy = GenerationType.AUTO) val id: Long,
                                 @BeanProperty @(Column@field)(name = "VERSION") @(Version@field) val version: Long) {
  def this() = this(-1, -1)
}

/**
 * Represents a player. A player has a name, initial and current ELO ranking.
 */
@Entity
@Table(name = "PLAYER")
@NamedQueries(Array(
  new NamedQuery(name = "Player.all", query = "select p from Player p"),
  new NamedQuery(name = "Player.byName", query = "select p from Player p where p.name = :name")
))
case class Player(
                   @BeanProperty
                   @(JsonProperty@field)("name")
                   @(Column@field)(name = "NAME")
                   name: String) extends PersistentEntity {

  def this() = this(null)
}

/**
 * Represents a team. A team contains of et least one player and might have a name.
 */
@Entity
@Table(name = "TEAM")
case class Team(
                 @BeanProperty @(Column@field)(name = "NAME") name: String) extends PersistentEntity {

  def this() = this(null)
}

/**
 * Represents a role that a player plays in a match round.
 */
@Entity
@Table(name = "ROLE")
case class Role(
                 @BeanProperty @(Column@field)(name = "NAME") name: String) extends PersistentEntity {

  def this() = this(null)
}

/**
 * Represents a competition discipline. Every discipline has a name and defines the number of teams, which
 * have to play a number of rounds until the winner is determined.
 */
@Entity
@Table(name = "DISCIPLINE")
case class Discipline(
                       @BeanProperty @(Column@field)(name = "NAME") name: String,
                       @BeanProperty @(Column@field)(name = "TEAMS") numberOfTeams: Int,
                       @BeanProperty @(Column@field)(name = "ROUNDS") numberOfRounds: Int) extends PersistentEntity {

  def this() = this(null, 2, 1)
}

/**
 * Represents a tournament.
 */
@Entity
@Table(name = "TOURNAMENT")
case class Tournament(
                       @BeanProperty @(Column@field)(name = "NAME") name: String,
                       @BeanProperty @(Column@field)(name = "START") start: Date,
                       @BeanProperty @(Column@field)(name = "END") end: Date) extends PersistentEntity {

  def this() = this(null, null, null)
}

/**
 * Represents a match.
 */
@Entity
@Table(name = "MATCH")
case class Match(
                  @BeanProperty @(Column@field)(name = "MATCH_DATE") matchDate: Date,
                  @BeanProperty @(Column@field)(name = "DESCRIPTION") description: String) extends PersistentEntity {

  def this() = this(null, null)
}