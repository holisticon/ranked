package de.holisticon.ranked.view.wall

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.MatchCreated
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Component
@TrackingProcessor
@ProcessingGroup("Wall")
@RestController
class WallView {

  val matches: MutableList<Match> = mutableListOf()

  @EventHandler
  fun on(e: MatchCreated) {
    matches.add(Match(teamRed = e.teamRed, teamBlue = e.teamBlue, matchSets = e.matchSets))
  }

  @GetMapping("/wall/matches")
  fun matches() = matches
}




data class Match(
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<MatchSet>
)
