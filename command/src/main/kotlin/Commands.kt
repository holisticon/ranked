package de.holisticon.ranked.command.api

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import io.swagger.annotations.ApiModel
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreatePlayer(
  @TargetAggregateIdentifier
  @get: Valid
  val userName: UserName
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

data class WinMatch(
  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String,
  @get: Valid
  val winner: Team,
  @get: Valid
  val looser: Team
)


data class ParticipateInMatch(
  @TargetAggregateIdentifier
  @get: Valid
  val player: UserName,
  @get: NotEmpty
  val matchId: String
)

data class UpdatePlayerRanking(
  @TargetAggregateIdentifier
  @get: Valid
  val player: UserName,
  @get: NotEmpty
  val matchId: String,
  val eloRanking: Int
)
