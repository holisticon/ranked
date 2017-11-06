package de.holisticon.ranked.model

import cz.jirutka.validator.spring.SpELAssert
import javax.validation.Valid
import javax.validation.constraints.Size


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
@SpELAssert(value = "!player1.equals(player2)", message = "{ranked.team.two.players}")
data class Team(
  @get: Valid
  val player1: UserName,

  @get: Valid
  val player2: UserName
)

data class MatchSet(
  val goalsRed: Int,
  val goalsBlue: Int,
  val offenseRed: UserName,
  val offenseBlue: UserName
)
