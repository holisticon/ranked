package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.PlayerWon
import de.holisticon.ranked.model.event.TeamWon
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RestController

@Component
@TrackingProcessor
@ProcessingGroup("PlayerLeaderBoard")
@RestController
class PlayerLeaderBoardView {

  val map : Map<Team, Int> = mutableMapOf()


  @EventHandler
  fun on(e: PlayerWon) = map.plus(e.player to 1)
}
