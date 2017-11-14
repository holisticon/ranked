package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.PlayerWon
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@TrackingProcessor
@ProcessingGroup(PlayerLeaderBoardView.NAME)
@RestController
@RequestMapping(value = "/view")
class PlayerLeaderBoardView {

  companion object : KLogging() {
    const val NAME = "PlayerLeaderBoard"
  }

  val map : Map<Team, Int> = mutableMapOf()

  @EventHandler
  fun on(e: PlayerWon) = map.plus(e.player to 1)
}
