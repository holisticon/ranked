package de.holisticon.ranked.view;

import de.holisticon.ranked.view.leaderboard.PlayerLeaderBoardView
import de.holisticon.ranked.view.player.PlayerViewService
import de.holisticon.ranked.view.wall.WallView
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
@ComponentScan
class ViewConfiguration {

  // TODO why do we need this?
  @Bean
  fun wall() = WallView

  @Bean
  fun leaderBoard() = PlayerLeaderBoardView

  @Bean
  fun playerService() = PlayerViewService

}
