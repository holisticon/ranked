package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.axon.TrackingProcessor
import mu.KLogging
import org.axonframework.config.ProcessingGroup
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
}
