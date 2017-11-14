package de.holisticon.ranked.model.event

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import java.time.LocalDate
import java.time.LocalDateTime

data class PlayerCreated(
  val userName: UserName,
  val displayName: String,
  val date: LocalDateTime = LocalDateTime.now()
)

data class MatchCreated(
  val matchId: String,
  val date: LocalDateTime = LocalDateTime.now(),
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<MatchSet>,
  val tournamentId: String?
)

data class TeamWonMatchSet(
  val team: Team,
  val looser: Team,
  val offense: UserName,
  val date: LocalDateTime = LocalDateTime.now()
)

data class TeamWonMatch(
  val team: Team,
  val looser: Team,
  val date: LocalDateTime = LocalDateTime.now()
)

data class PlayerWonMatchSet(
  val player: UserName,
  val position: PlayerPosition,
  val teammate: UserName,
  val date: LocalDateTime = LocalDateTime.now()
)

data class PlayerWonMatch(
  val player: UserName,
  val teammate: UserName,
  val date: LocalDateTime = LocalDateTime.now()
)

enum class PlayerPosition {
  OFFENSE, DEFENSE
}