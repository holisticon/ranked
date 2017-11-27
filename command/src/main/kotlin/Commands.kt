package de.holisticon.ranked.command.api

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.command.aggregate.Match.Companion.BEST_OF
import de.holisticon.ranked.command.aggregate.Match.Companion.SCORE_TO_WIN_MATCH
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import io.swagger.annotations.ApiModel
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class CreatePlayer(
  @TargetAggregateIdentifier
  @get: Valid
  val userName: UserName,

  @get: NotEmpty
  val displayName: String,

  val date: LocalDateTime = LocalDateTime.now()
)

@ApiModel
@SpELAssert.List(
  SpELAssert(value = "disjunct()", message = "{ranked.createMatch.disjunct}"),
  SpELAssert(value = "correctOffense()", message = "{ranked.createMatch.offense}")
)
data class CreateMatch(

  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String = UUID.randomUUID().toString(),

  val date: LocalDateTime = LocalDateTime.now(),

  val teamRed: Team,

  val teamBlue: Team,

  @get: Size(min = SCORE_TO_WIN_MATCH, max = BEST_OF)
  val matchSets: List<MatchSet>,

  val tournamentId: String? = null
) {

  fun disjunct() = setOf(teamRed.player1, teamRed.player2).intersect(setOf(teamBlue.player1, teamBlue.player2)).isEmpty()
  fun correctOffense() = matchSets.filter{ s -> !teamRed.hasMember(s.offenseRed) || !teamBlue.hasMember(s.offenseBlue) }.isEmpty()
}

data class WinMatch(
  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String,
  val winner: Team,
  val looser: Team
)
