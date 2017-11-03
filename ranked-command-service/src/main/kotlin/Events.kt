package de.holisticon.ranked.command.event

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import java.time.LocalDateTime
import java.util.*

data class PlayerCreated(val userName:UserName, val displayName:String)


data class MatchCreated(
  val matchId: String,
  val date: LocalDateTime,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: Array<MatchSet>,
  val tournamentId: String?
)
