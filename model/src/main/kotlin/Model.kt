package de.holisticon.ranked.model

import cz.jirutka.validator.spring.SpELAssert
import org.hibernate.validator.constraints.Range
import javax.validation.Valid
import javax.validation.constraints.Size

/**
 * ValueBean representing a unique user name (login value)
 *
 * @property value the unique userName, min length 4
 */
data class UserName(
  @get: Size(min = 4, message = "{ranked.model.userName.too.short}") // this is the kotlin way of using jsr-303
  val value: String
)


/**
 * ValueBean representing a kicker team of two *different* players.
 *
 * @property player1 the first player
 * @property player2 the second player
 */
@SpELAssert(value = "player1 != player2", message = "{ranked.model.team.two.players}")
data class Team(

  @get: Valid
  val player1: UserName,

  @get: Valid
  val player2: UserName
) {

  companion object {
    const val BLUE = "blue"
    const val RED = "red"
  }

  private val players by lazy { setOf(player1, player2) }

  infix fun hasMember(userName: UserName) = players.contains(userName)

  override fun hashCode(): Int {
    return 17 * (player1.hashCode() * player2.hashCode())
  }

  override fun equals(other: Any?): Boolean {
    return other is Team && this.players == other.players
  }
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
  @get: Range(min = 0, max = 6, message = "{ranked.model.matchSet.goals}")
  val goalsRed: Int,

  @get: Range(min = 0, max = 6, message = "{ranked.model.matchSet.goals}")
  val goalsBlue: Int,

  val offenseRed: UserName,

  val offenseBlue: UserName
) {

  fun winner() = if (goalsRed == 6)  Team.RED else Team.BLUE
}
