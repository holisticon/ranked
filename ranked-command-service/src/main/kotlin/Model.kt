package de.holisticon.ranked.model

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import javax.validation.*
import javax.validation.constraints.Size
import kotlin.reflect.KClass


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
@DifferentPlayers
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

class DifferentPlayersValidator : ConstraintValidator<DifferentPlayers, Team> {
  override fun isValid(team: Team, ctx: ConstraintValidatorContext): Boolean {
    val isValid = team.player1 != team.player2

    return isValid
  }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = arrayOf(DifferentPlayersValidator::class))
@Documented
annotation class DifferentPlayers(
  val constraints: Array<KClass<*>> = arrayOf(),
  val groups: Array<KClass<*>> = arrayOf(),
  val payload: Array<KClass<out Payload>> = arrayOf(),
  val message: String = "totally wrong, dude!"
)
