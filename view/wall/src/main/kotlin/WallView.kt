package de.holisticon.ranked.view.wall

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.MatchCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.context.ApplicationEventPublisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@TrackingProcessor
@ProcessingGroup(WallView.NAME)
@RestController
@RequestMapping(value = "/view")
class WallView(val eventPublisher: ApplicationEventPublisher) {

  companion object : KLogging() {
    const val NAME = "Wall"
  }

  val matches: MutableList<Match> = mutableListOf()

  @EventHandler
  fun on(e: MatchCreated) {
    matches.add(Match(teamRed = e.teamRed, teamBlue = e.teamBlue, matchSets = e.matchSets, matchId = e.matchId, date = e.date))
    logger.info { "Match created for ${e}" }
  }

  @GetMapping("/wall/matches")
  fun matches() = matches

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
