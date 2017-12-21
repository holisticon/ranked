package de.holisticon.ranked.view;

import de.holisticon.ranked.view.leaderboard.PlayerLeaderBoardView
import de.holisticon.ranked.view.player.PlayerViewService
import de.holisticon.ranked.view.wall.WallView
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ViewConfiguration {

  // TODO why do we need this?
  @Bean
  fun wall() = WallView

  @Bean
  fun leaderBoard() = PlayerLeaderBoardView

  @Bean
  fun playerService() = PlayerViewService

}
