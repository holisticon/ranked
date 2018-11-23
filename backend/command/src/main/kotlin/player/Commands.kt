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
  val userName: UserName,
  val displayName: String,
  val imageUrl: String
) {
  constructor(displayName: String, imageUrl: String): this(
    displayName = displayName,
    imageUrl = imageUrl,
    /**
     * Calculate a username out of display name, putting the parts together, going lower case and replacing the umlauts.
     */
    userName = UserName(displayName
        .replace(" ", "")
        .replace("ü", "ue")
        .replace("ä", "ae")
        .replace("ö", "oe")
        .replace("ß", "ss")
    )
  )
}

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

