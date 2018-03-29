@file:Suppress("UNUSED")

package de.holisticon.ranked.view.wall

import de.holisticon.ranked.model.AbstractMatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.service.user.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@ProcessingGroup(WallView.NAME)
@Api(tags = ["News wall"])
@RestController
@RequestMapping(value = ["/view"])
class WallView(val userService: UserService) {

  companion object : KLogging() {
    const val NAME = "Wall"
  }

  val matches: MutableList<Match> = mutableListOf()
  val playerWins: MutableList<PlayerWin> = mutableListOf()
  val teamWins: MutableList<TeamWin> = mutableListOf()

  @ApiOperation(value = "Lists matches.")
  @GetMapping("/wall/matches")
  fun matches() = matches

  @ApiOperation(value = "Lists player wins.")
  @GetMapping("/wall/players")
  fun playerWins() = playerWins

  @ApiOperation(value = "Lists team wins.")
  @GetMapping("/wall/teams")
  fun teamWins() = teamWins

  @Deprecated("use PlayerView")
  @ApiOperation(value = "Lists all users")
  @GetMapping("/user")
  fun users() = userService.loadAll().sortedBy { it.id }

  @Deprecated("use PlayerView")
  @ApiOperation(value = "Lists all users")
  @GetMapping("/user/{id}")
  fun users(@PathVariable("id") id: String) = userService.loadUser(id)

  @EventHandler
  fun on(e: MatchCreated, @Timestamp timestamp: Instant) {
    matches.add(Match(teamRed = e.teamRed, teamBlue = e.teamBlue, matchSets = e.matchSets, matchId = e.matchId, date = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Match created for ${e.matchId}" }
  }

  @EventHandler
  fun on(e: TeamWonMatch, @Timestamp timestamp: Instant) {
    teamWins.add(TeamWin(e.team, e.looser, Type.MATCH, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Team ${e.team} won a match vs ${e.looser} " }

    playerWins.add(PlayerWin(e.team.player1, Type.MATCH, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Player ${e.team.player1} won a match." }

    playerWins.add(PlayerWin(e.team.player2, Type.MATCH, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Player ${e.team.player2} won a match." }
  }

  @EventHandler
  fun on(e: TeamWonMatchSet, @Timestamp timestamp: Instant) {
    teamWins.add(TeamWin(e.team, e.looser, Type.SET, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Team ${e.team} won a set vs ${e.looser}" }

    playerWins.add(PlayerWin(e.team.player1, Type.SET, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Player ${e.team.player1} won a set." }

    playerWins.add(PlayerWin(e.team.player2, Type.SET, LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
    logger.info { "Player ${e.team.player2} won a set." }
  }

  @EventHandler
  fun on(e: PlayerParticipatedInMatch) {
    logger.info { "Player ${e.player} with ranking ${e.eloRanking} played in match ${e.matchId}" }
  }
}

data class Match(
  val matchId: String,
  val date: LocalDateTime,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<AbstractMatchSet>
)

data class PlayerWin(
  val username: UserName,
  val type: Type,
  val date: LocalDateTime
)

data class TeamWin(
  val team: Team,
  val looser: Team,
  val type: Type,
  val date: LocalDateTime
)

enum class Type {
  MATCH, SET
}

