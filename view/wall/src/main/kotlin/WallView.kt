package de.holisticon.ranked.view.wall

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.model.event.internal.ReplayTrackingProcessor
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


@TrackingProcessor
@ProcessingGroup(WallView.NAME)
@Api(tags = arrayOf("News wall"))
@RestController
@RequestMapping(value = "/view")
class WallView(val eventPublisher: ApplicationEventPublisher) {

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

  @EventHandler
  fun on(e: MatchCreated) {
    matches.add(Match(teamRed = e.teamRed, teamBlue = e.teamBlue, matchSets = e.matchSets, matchId = e.matchId, date = e.date))
    logger.info { "Match created for ${e.matchId}" }
  }

  @EventHandler
  fun on(e: TeamWonMatch) {
    teamWins.add(TeamWin(e.team, e.looser, Type.MATCH, e.date))
    logger.info { "Team ${e.team} won a match vs ${e.looser} " }
  }

  @EventHandler
  fun on(e: TeamWonMatchSet) {
    teamWins.add(TeamWin(e.team, e.looser, Type.SET, e.date))
    logger.info { "Team ${e.team} won a set vs ${e.looser}" }
  }

  @EventHandler
  fun on(e: PlayerWonMatch) {
    playerWins.add(PlayerWin(e.player, Type.MATCH, e.date))
    logger.info { "Player ${e.player} won a match " }
  }

  @EventHandler
  fun on(e: PlayerWonMatchSet) {
    playerWins.add(PlayerWin(e.player, Type.SET, e.date))
    logger.info { "Player ${e.player} won a set " }
  }


//  @GetMapping("/management/replay")
//  fun replay() = eventPublisher.publishEvent(ReplayTrackingProcessor(NAME))
}

data class Match(
  val matchId: String,
  val date: LocalDateTime,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<MatchSet>
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

