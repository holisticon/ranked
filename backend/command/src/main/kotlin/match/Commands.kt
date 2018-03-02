package de.holisticon.ranked.command.api

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.model.AbstractMatchSet
import de.holisticon.ranked.model.Team
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
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
  @field: NotEmpty
  val matchId: String = UUID.randomUUID().toString(),

  @field: Valid
  val teamRed: Team,

  @field: Valid
  val teamBlue: Team,

  @SpELAssert("@matchService.validateMatch(#this)", message = "{ranked.createMatch.finished}")
  val matchSets: List<AbstractMatchSet>,

  val tournamentId: String? = null,

  val startTime: LocalDateTime = LocalDateTime.now()
) {

  fun disjunct() = setOf(teamRed.player1, teamRed.player2).intersect(setOf(teamBlue.player1, teamBlue.player2)).isEmpty()
  fun correctOffense() = matchSets.filter { s -> !teamRed.hasMember(s.offenseRed) || !teamBlue.hasMember(s.offenseBlue) }.isEmpty()
}

/**
 * Command towards Match aggregate to mark the match as won.
 */
data class WinMatch(
  @TargetAggregateIdentifier
  @field: NotEmpty
  val matchId: String,
  @field: Valid
  val winner: Team,
  @field: Valid
  val looser: Team
)


