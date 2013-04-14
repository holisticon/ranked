package de.holisticon.ranked.api.model

import java.util.{Collections, Date}

import scala.annotation.meta.{getter, field}
import scala.beans.BeanProperty

import javax.persistence._
import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import java.util

@MappedSuperclass
abstract class Versioned(@BeanProperty @(Column@field)(name = "VERSION") @(Version@field) @(JsonIgnore@field) val version: Long) {
  protected def this() = this(-1)

}


/**
 * Superclass for all persistent entities.
 */
@MappedSuperclass
abstract class PersistentEntity(
                                 @BeanProperty @(Column@field)(name = "ID") @(Id@field) @(GeneratedValue@field)(strategy = GenerationType.AUTO) val id: Long
                                 ) extends Versioned {
  protected def this() = this(-1)
}

/**
 * Represents a competition discipline. Every discipline has a name and defines the number of teams, which
 * have to play a number of rounds until the winner is determined.
 */
@Entity
@Table(name = "DISCIPLINE")
@NamedQueries(Array(
  new NamedQuery(name = "Discipline.all", query = "select p from Discipline p"),
  new NamedQuery(name = "Discipline.byName", query = "select p from Discipline p where p.name = :name")
))
case class Discipline(
                       @BeanProperty @(Column@field)(name = "NAME", nullable = false, unique = true) name: String,
                       @BeanProperty @(Column@field)(name = "TEAMS", nullable = false) numberOfTeams: Int,
                       @BeanProperty @(Column@field)(name = "ROUNDS", nullable = false) numberOfRounds: Int,
                       @BeanProperty @(OneToMany@field)(cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, mappedBy = "discipline") shops: java.util.Set[Role] = Collections.emptySet(),
                       @BeanProperty @(OneToMany@field)(cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, mappedBy = "discipline") matches: java.util.Set[Match] = Collections.emptySet(),
                       @BeanProperty @(OneToMany@field)(cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, mappedBy = "discipline") rankings: java.util.Set[Ranking] = Collections.emptySet()) extends PersistentEntity {
  private def this() = this(null, -1, -1)

}

/**
 * Represents a match.
 */
@Entity
@Table(name = "MATCH")
case class Match(
                  @BeanProperty @(Column@field)(name = "MATCH_DATE", nullable = false)@(Temporal@field)(TemporalType.TIMESTAMP) matchDate: Date,
                  @BeanProperty @(Column@field)(name = "DESCRIPTION") description: String,
                  @BeanProperty @(ManyToOne@field) @(JoinColumn@field)(name = "DISCIPLINE_ID", nullable = false) discipline: Discipline,
                  @BeanProperty @(ManyToOne@field) @(JoinColumn@field)(name = "TOURNAMENT_ID", nullable = false) tournament: Tournament) extends PersistentEntity {

  private def this() = this(null, null, null, null)
}

/**
 * Represents a player. A player has a name, initial and current ELO ranking.
 */
@Entity
@Table(name = "RANKING")
@AssociationOverrides(Array(
  new AssociationOverride(name = "id.player", joinColumns = Array(new JoinColumn(name = "PLAYER_ID"))),
  new AssociationOverride(name = "id.discipline", joinColumns = Array(new JoinColumn(name = "DISCIPLINE_ID")))
))
case class Ranking(
                    @BeanProperty @(EmbeddedId@field)id: RankingId,
                    @BeanProperty @(Column@field)(name = "INITIAL_RANKING") initRanking: Int,
                    @BeanProperty @(Column@field)(name = "RANKING") currentRanking: Int,
                    @BeanProperty @(ManyToOne@field) @(JoinColumn@field)(name = "DISCIPLINE_ID", insertable = false, updatable = false) discipline: Discipline = null,
                    @BeanProperty @(ManyToOne@field) @(JoinColumn@field)(name = "PLAYER_ID", insertable = false, updatable = false) player: Player = null
                    ) extends Versioned {
  private def this() = this(null, 0, 0)

}

@Embeddable
case class RankingId(
                      @BeanProperty @(ManyToOne@field)(optional = false) player: Player,
                      @BeanProperty @(ManyToOne@field)(optional = false) discipline: Discipline) extends Serializable {
  private def this() = this(null, null)
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
                   @BeanProperty @(JsonProperty@field)("name") @(Column@field)(name = "NAME",unique = true) name: String,
                   @BeanProperty @(ManyToMany@field) @(JoinTable@field)(name = "PLAYER_IN_TEAM", joinColumns = Array(new JoinColumn(name = "PLAYER_ID")), inverseJoinColumns = Array(new JoinColumn(name = "TEAM_ID"))) teams: java.util.Set[Team] = Collections.emptySet(),
                   @BeanProperty @(OneToMany@field)(cascade = Array(CascadeType.REMOVE), mappedBy = "player") rankings: java.util.Set[Ranking] = Collections.emptySet()) extends PersistentEntity {

  private def this() = this(null, null, null)
}

/**
 * Represents a role that a player plays in a match round.
 */
@Entity
@Table(name = "ROLE")
@NamedQueries(Array(
  new NamedQuery(name = "Role.all", query = "select r from Role r")
))
case class Role(
                 @BeanProperty @(Column@field)(name = "NAME") name: String,
                 @BeanProperty @(ManyToOne@field) @(JoinColumn@field)(name = "DISCIPLINE_ID", nullable = false) discipline: Discipline) extends PersistentEntity {
  private def this() = this(null, null)
}


/**
 * Represents a team. A team contains of et least one player and might have a name.
 */
@Entity
@Table(name = "TEAM")
@NamedQueries(Array(
  new NamedQuery(name = "Team.all", query = "select t from Team t"),
  new NamedQuery(name = "Team.byName", query = "select t from Team t where t.name = :name")
))
case class Team(
                 @BeanProperty @(Column@field)(name = "NAME") name: String,
                 @BeanProperty @(ManyToMany@field) @(JoinColumn@field)(name = "PLAYER_ID")
                 @(JoinTable@field)(name = "PLAYER_IN_TEAM", joinColumns = Array(new JoinColumn(name = "TEAM_ID")), inverseJoinColumns = Array(new JoinColumn(name = "PLAYER_ID"))) players: java.util.Set[Player] = Collections.emptySet()) extends PersistentEntity {

  private def this() = this(null, null)
}

/**
 * Represents a tournament.
 */
@Entity
@Table(name = "TOURNAMENT")
@NamedQueries(Array(
  new NamedQuery(name = "Tournament.all", query = "select t from Tournament t")
))
case class Tournament(
                       @BeanProperty @(Column@field)(name = "DISCIPLINE", nullable = false) discipline: Discipline,
                       @BeanProperty @(Column@field)(name = "NAME", nullable = false) name: String,
                       @BeanProperty @(Column@field)(name = "START", nullable = false) @(Temporal@field)(TemporalType.TIMESTAMP) start: Date,
                       @BeanProperty @(Column@field)(name = "END", nullable = false) @(Temporal@field)(TemporalType.TIMESTAMP) end: Date,
                       @BeanProperty @(OneToMany@field)(cascade = Array(CascadeType.REMOVE), fetch = FetchType.LAZY, mappedBy = "tournament") matches: java.util.Set[Match] = Collections.emptySet()) extends PersistentEntity {

  private def this() = this(null, null, null, null)
}