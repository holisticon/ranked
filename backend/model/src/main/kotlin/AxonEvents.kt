package de.holisticon.ranked.model.event

import de.holisticon.ranked.model.*
import org.axonframework.serialization.Revision
import java.time.LocalDateTime

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

enum class PlayerPosition {
  OFFENSE, DEFENSE
}
