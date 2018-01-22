package de.holisticon.ranked.command.api

import de.holisticon.ranked.model.Elo
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

/**
 * Create command for Player aggregate.
 */
data class CreatePlayer(
  @field: Valid
  @TargetAggregateIdentifier
  val userName: UserName
)

/**
 * Check if player aggregate already exists.
 */
data class CheckPlayer(
  @field: Valid
  @TargetAggregateIdentifier
  val userName: UserName
)

/**
 * Command towards player aggregate to take part in the match.
 */
data class ParticipateInMatch(
  @TargetAggregateIdentifier
  @field: Valid
  val userName: UserName,

  @field: NotEmpty
  val matchId: String
)

/**
 * Resets participations if any
 */
data class CancelParticipation(
  @TargetAggregateIdentifier
  @field: Valid
  val userName: UserName
)

/**
 * Command towards player aggregate to update the ranking.
 */
data class UpdatePlayerRanking(
  @TargetAggregateIdentifier
  @field: Valid
  val userName: UserName,

  @field: NotEmpty
  val matchId: String,

  val eloRanking: Elo
)

