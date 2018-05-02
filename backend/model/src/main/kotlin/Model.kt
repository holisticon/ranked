package de.holisticon.ranked.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import cz.jirutka.validator.spring.SpELAssert
import org.hibernate.validator.constraints.Range
import java.io.Serializable
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

  /**
   * Representing the Elo Value of a player or team.
   */
typealias Elo = Int
typealias ImageUrl = String

/**
 * Represents the team color.
 */
enum class TeamColor {
  RED, BLUE
}

/**
 * ValueBean representing a unique user name (login value)
 *
 * @property value the unique id, min length 4
 */
data class UserName(
  @field:Size(min = 4, message = "{ranked.model.id.too.short}") // this is the kotlin way of using jsr-303
  val value: String
) : Serializable {

  override fun hashCode(): Int = 17 * value.hashCode()
  override fun equals(other: Any?): Boolean = other is UserName && this.value == other.value
  override fun toString(): String = value
}

data class Player(
  val userName: UserName,
  val displayName: String,
  val imageUrl: String,
  val eloRanking: Int?
)

/**
 * ValueBean representing a kicker team of two *different* players.
 *
 * @property player1 the first player
 * @property player2 the second player
 */
@SpELAssert(value = "player1 != player2", message = "{ranked.model.team.two.players}")
data class Team(

  @field:Valid
  val player1: UserName,

  @field:Valid
  val player2: UserName
) {

  private val players by lazy { setOf(player1, player2) }

  infix fun hasMember(userName: UserName) = players.contains(userName)

  override fun hashCode(): Int {
    return 17 * (player1.hashCode() * player2.hashCode())
  }

  override fun equals(other: Any?): Boolean {
    return other is Team && this.players == other.players
  }
}

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes(value = [
  JsonSubTypes.Type(value = MatchSet::class, name = "result"),
  JsonSubTypes.Type(value = TimedMatchSet::class, name = "timestamp")
])
sealed class AbstractMatchSet {
  abstract val goalsRed: Int
  abstract val goalsBlue: Int
  abstract val offenseRed: UserName
  abstract val offenseBlue: UserName

  fun winner() = if (goalsRed > goalsBlue) TeamColor.RED else TeamColor.BLUE

  // TODO fix this and replace by the property!
  // this implies to move validation logic to somewhere else (e.G. a service) -> problem with dependencies....
  // could be executed by SpELAssert, but the properties needs to be available here...
  companion object {
    const val SCORE_TO_WIN: Long = 999
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
@SpELAssert(value = "goalsBlue != goalsRed", message = "{ranked.model.matchSet.ended}")
data class MatchSet(
  @field: Range(min = 0, max = AbstractMatchSet.SCORE_TO_WIN, message = "{ranked.model.matchSet.goals}")
  override val goalsRed: Int,
  @field: Range(min = 0, max = AbstractMatchSet.SCORE_TO_WIN, message = "{ranked.model.matchSet.goals}")
  override val goalsBlue: Int,
  @field: Valid
  override val offenseRed: UserName,
  @field: Valid
  override val offenseBlue: UserName
) : AbstractMatchSet()


@SpELAssert(value = "goalsBlue != goalsRed", message = "{ranked.model.matchSet.ended}")
data class TimedMatchSet(
  @field: NotEmpty(message = "{ranked.model.matchSet.empty}")
  val goals: List<Pair<TeamColor, LocalDateTime>>,
  @field: Valid
  override val offenseRed: UserName,
  @field: Valid
  override val offenseBlue: UserName,
  @field: Range(min = 0, max = AbstractMatchSet.SCORE_TO_WIN, message = "{ranked.model.matchSet.goals}")
  override val goalsBlue: Int = goalsByTeamColor(goals, TeamColor.BLUE),
  @field: Range(min = 0, max = AbstractMatchSet.SCORE_TO_WIN, message = "{ranked.model.matchSet.goals}")
  override val goalsRed: Int = goalsByTeamColor(goals, TeamColor.RED)
) : AbstractMatchSet()

private fun goalsByTeamColor(goals: List<Pair<TeamColor, LocalDateTime>>, tc: TeamColor): Int {
  val groupedGoals = goals.groupBy { it.first }.filter { it.key == tc }.map { it.value.count() }
  return if (groupedGoals.isEmpty()) {
    0
  } else {
    groupedGoals.first()
  }
}
