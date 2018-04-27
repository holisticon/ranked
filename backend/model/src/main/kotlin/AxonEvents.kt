package de.holisticon.ranked.model.event

import de.holisticon.ranked.model.*
import org.axonframework.serialization.Revision
import java.time.LocalDateTime

enum class PlayerPosition {
  OFFENSE, DEFENSE
}

/**
 * Player events.
 */
@Revision("2")
data class PlayerCreated(
  val userName: UserName,
  val displayName: String,
  val imageUrl: String,
  val initialElo: Int
)

data class PlayerExists(
  val userName: UserName
)

data class PlayerParticipatedInMatch(
  val player: UserName,
  val matchId: String,
  val eloRanking: Elo
)

data class ParticipationCancelled(
  val player: UserName
)

data class PlayerRankingChanged(
  val player: UserName,
  val eloRanking: Elo
)

/**
 * Match events.
 */
data class MatchCreated(
  val matchId: String,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<AbstractMatchSet>,
  val startTime: LocalDateTime,
  val tournamentId: String?
)

data class TeamWonMatchSet(
  val team: Team,
  val looser: Team,
  val offense: UserName,
  val matchId: String
)

data class TeamWonMatch(
  val matchId: String,
  val team: Team,
  val looser: Team
)

/**
 * Team events
 */

data class TeamCreated(
  val id: String,
  val team: Team,
  val name: String,
  val imageUrl: String
)

data class TeamRenamed(
  val id: String,
  val name: String
)


