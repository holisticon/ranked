package de.holisticon.ranked.command.cmd

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*

data class CreatePlayer(
  @TargetAggregateIdentifier
  val userName: UserName,
  val displayName: String
)

data class CreateMatch(
  @TargetAggregateIdentifier
  val matchId: String = UUID.randomUUID().toString(),
  val date: LocalDateTime = LocalDateTime.now(),
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: Array<MatchSet>,
  val tournamentId: String? = null
)
