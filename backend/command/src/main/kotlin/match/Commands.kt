package de.holisticon.ranked.command.api

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Create command towards Match aggregate to create the match.
 */
@SpELAssert.List(
  SpELAssert(value = "disjunct()", message = "{ranked.createMatch.disjunct}"),
  SpELAssert(value = "correctOffense()", message = "{ranked.createMatch.offense}")
)
data class CreateMatch(

  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String = UUID.randomUUID().toString(),
  @get: Valid
  val teamRed: Team,
  @get: Valid
  val teamBlue: Team,
  @SpELAssert("@matchService.validateMatch(#this)", message = "{ranked.createMatch.finished}")
  val matchSets: List<MatchSet>,
  val tournamentId: String? = null
) {

  fun disjunct() = setOf(teamRed.player1, teamRed.player2).intersect(setOf(teamBlue.player1, teamBlue.player2)).isEmpty()
  fun correctOffense() = matchSets.filter{ s -> !teamRed.hasMember(s.offenseRed) || !teamBlue.hasMember(s.offenseBlue) }.isEmpty()
}

/**
 * Command towards Match aggregate to mark the match as won.
 */
data class WinMatch(
  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String,
  @get: Valid
  val winner: Team,
  @get: Valid
  val looser: Team
)


