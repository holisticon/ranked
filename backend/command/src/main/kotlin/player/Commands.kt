package de.holisticon.ranked.command.api

import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

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

