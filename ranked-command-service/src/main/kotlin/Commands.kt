package de.holisticon.ranked.command.cmd

import cz.jirutka.validator.spring.SpELAssert
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

data class CreatePlayer(
  @TargetAggregateIdentifier
  @get: Valid
  val userName: UserName,

  @get: NotEmpty
  val displayName: String
)

@SpELAssert(value = "disjunct()", message = "{ranked.createMatch.disjunct}")
data class CreateMatch(
  @TargetAggregateIdentifier
  @get: NotEmpty
  val matchId: String = UUID.randomUUID().toString(),

  val date: LocalDateTime = LocalDateTime.now(),

  val teamRed: Team,

  val teamBlue: Team,

  val matchSets: Array<MatchSet>,

  val tournamentId: String? = null
) {

  fun disjunct() = setOf(teamRed.player1, teamRed.player2).intersect(setOf(teamBlue.player1, teamBlue.player2)).isEmpty()

}
