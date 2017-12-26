package de.holisticon.ranked.command.api

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.properties.RankedProperties
import io.swagger.annotations.ApiModel
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/*************************************************************
 *  Player Commands
 ************************************************************* */

/**
 * Create command for Player aggregate.
 */
data class CreatePlayer(
  @get: Valid
  @TargetAggregateIdentifier
  val userName: UserName
)

/**
 * Check if player aggregate already exists.
 */
data class CheckPlayer(
  @get: Valid
  @TargetAggregateIdentifier
  val userName: UserName
)

/**
 * Command towards player aggregate to take part in the match.
 */
data class ParticipateInMatch(
  @TargetAggregateIdentifier
  @get: Valid
  val userName: UserName,
  @get: NotEmpty
  val matchId: String
)

/**
 * Command towards player aggregate to update the ranking.
 */
data class UpdatePlayerRanking(
  @TargetAggregateIdentifier
  @get: Valid
  val userName: UserName,
  @get: NotEmpty
  val matchId: String,
  val eloRanking: Int
)

/*************************************************************
 *  Match Commands
 ************************************************************* */

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


data class CreateConfiguration(
  @TargetAggregateIdentifier
  val id: String = ID,
  val properties : RankedProperties
) {
  companion object {
    const val ID = "1"
  }
}


data class UpdateConfiguration(
  @TargetAggregateIdentifier
  val id: String = CreateConfiguration.ID,
  val properties : RankedProperties
)

data class CheckConfiguration(
  @TargetAggregateIdentifier
  val id: String = CreateConfiguration.ID
)
