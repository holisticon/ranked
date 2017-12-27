package de.holisticon.ranked.view.leaderboard

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProcessingGroup(PlayerLeaderBoardView.NAME)
@RestController
@RequestMapping(value = "/view")
class PlayerLeaderBoardView {

  companion object : KLogging() {
    const val NAME = "PlayerLeaderBoard"
  }
}
