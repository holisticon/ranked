@file:Suppress("UNUSED")
package de.holisticon.ranked.view.player

import de.holisticon.ranked.model.Player
import de.holisticon.ranked.model.event.PlayerCreated
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProcessingGroup(PlayerViewService.NAME)
@RestController
@Api(tags = ["Players"])
@RequestMapping(value = ["/view"])
class PlayerViewService {

  companion object : KLogging() {
    const val NAME = "Player"
  }

  val players: MutableSet<Player> = mutableSetOf()

  @ApiOperation(value = "Lists all players")
  @GetMapping("/player")
  fun getAllPlayers() = players.sortedBy { it.userName.value }

  @ApiOperation(value = "Get player by userName")
  @GetMapping("/player/{userName}")
  fun users(@PathVariable("userName") userName: String) = players.find { userName == it.userName.value }


  @EventHandler
  fun on(e: PlayerCreated) {
    players.add(
      Player(
        userName = e.userName,
        displayName = e.displayName,
        imageUrl = e.imageUrl,
        eloRanking = e.initialElo
      ))
  }


}
