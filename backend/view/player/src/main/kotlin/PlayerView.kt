package de.holisticon.ranked.view.player

import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.PlayerRankingChanged
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Service

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
    players.add(
      Player(
        userName = e.userName,
        displayName = e.displayName,
        eloRanking = e.initialElo
      ))
  }

  @EventHandler
  fun on(e: PlayerRankingChanged) {
    // TODO update ranking
  }

    data class Player(
    val userName: UserName,
    val displayName: String,
    val eloRanking: Int
  )
}
