package de.holisticon.ranked.model

import cz.jirutka.validator.spring.SpELAssert
import net.minidev.json.annotate.JsonIgnore
import org.hibernate.validator.constraints.Range
import javax.validation.Valid
import javax.validation.constraints.Size
import javax.xml.bind.annotation.XmlTransient


/**
 * ValueBean representing a unique user name (login value)
 *
 * @property value the unique userName, min length 4
 */
data class UserName(
  @get: Size(min = 4) // this is the kotlin way of using jsr-303
  val value: String
)

/**
 * ValueBean representing a kicker team of two *different* players.
 *
 * @property player1 the first player
 * @property player2 the second player
 */
@SpELAssert(value = "player1 != null && player2 != null && player1 != player2", message = "{ranked.team.two.players}")
data class Team(
  @get: Valid
  val player1: UserName,

  @get: Valid
  val player2: UserName
) {

  infix fun hasMember(userName: UserName) = setOf(player1, player2).contains(userName)
}

/**
 * A MatchSet represents one Set of a Match (which will have two or three (best of three) of them).
 *
 * @property goalsRed number of goals scored by red (0 to 6)
 * @property goalsBlue number of goals scored by blue (0 to 6)
 * @property offenseRed the UserName of the player who played offense for red
 * @property offenseBlue the UserName of the player who played offense for blue
 */
data class MatchSet(
  @get: Range(min = 0, max = 6, message = "{ranked.matchSet.goals}")
  val goalsRed: Int,

  @get: Range(min = 0, max = 6, message = "{ranked.matchSet.goals}")
  val goalsBlue: Int,

  val offenseRed: UserName,

  val offenseBlue: UserName
)
