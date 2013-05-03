package de.holisticon.ranked.api.model

import java.util.Date
import com.fasterxml.jackson.annotation.JsonProperty
import scala.annotation.meta.field
import scala.beans.BeanProperty


/**
 * Represents a match of a discipline in a tournament with given description. The result of the match is stored
 * @param disciplineId discipline of the match
 * @param tournamentId tournament the match belongs to
 * @param description optional description of the match
 * @param teams participating teams
 * @param rounds list of rounds with results
 * @param time time of the match
 */
case class MatchResult(@BeanProperty @(JsonProperty@field)disciplineId: Long,
                 @BeanProperty @(JsonProperty@field)tournamentId: Long,
                 @BeanProperty @(JsonProperty@field)description: String,
                 @BeanProperty @(JsonProperty@field)teams: java.util.List[MatchTeam],
                 @BeanProperty @(JsonProperty@field)rounds: java.util.List[Round],
                 @BeanProperty @(JsonProperty@field)time: Date) {

}

/**
 * Represents a round of the match.
 * @param teamResults list of team results - one for every team.
 */
case class Round(@BeanProperty @(JsonProperty@field)teamResults: java.util.List[TeamResult]) {

}

/**
 * Represents a team, consisting of players.
 * @param members a team is a temporal set of players participating in a match.
 */
case class MatchTeam(@BeanProperty @(JsonProperty@field)members: java.util.List[Long]) {

}

/**
 * Represents the score gathered by the team with a given assignment of roles to players.
 * @param team team reached the result.
 * @param playerRoles assignment of roles to players
 * @param score reached score.
 */
case class TeamResult(@BeanProperty @(JsonProperty@field)team: MatchTeam,
                      @BeanProperty @(JsonProperty@field)playerRoles : java.util.List[PlayerRole],
                      @BeanProperty @(JsonProperty@field)score: BigDecimal) {

}

/**
 * Represents an assignment of a role to a player during a match.
 * @param playerId player reference.
 * @param roleId role in current match.
 */
case class PlayerRole(@BeanProperty @(JsonProperty@field)playerId: Long,
                      @BeanProperty @(JsonProperty@field)roleId: Long) {
}
