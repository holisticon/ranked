package de.holisticon.ranked.view.player

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service


@TrackingProcessor
@ProcessingGroup(PlayerViewService.NAME)
@Service
class PlayerViewService {

  companion object : KLogging() {
    const val NAME = "Player"
  }

  val players: MutableSet<Player> = mutableSetOf()

  fun getAllPlayers() = players.toMutableSet()

  @EventHandler
  fun on(e: PlayerCreated) {
    players.add(Player(userName = e.userName, displayName = e.displayName))
  }


  data class Player(val userName: UserName, val displayName: String)
}
