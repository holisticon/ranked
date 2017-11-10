package de.holisticon.ranked.model.event

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import java.time.LocalDateTime

data class PlayerCreated(val userName:UserName, val displayName:String)


data class MatchCreated(
  val matchId: String,
  val date: LocalDateTime,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<MatchSet>,
  val tournamentId: String?
)

data class TeamWon(
  val team : Team
)

data class PlayerWon(
  val player: UserName
)
