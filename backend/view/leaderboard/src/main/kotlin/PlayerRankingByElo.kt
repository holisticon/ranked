@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Elo
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.PlayerRankingChanged
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

const val PROCESSING_GROUP = "PlayerLeaderBoard"

@RestController
@RequestMapping(value = ["/view"])
class PlayerLeaderBoardView(
  private val playerRankingByElo: PlayerRankingByEloHandler
) {
  @GetMapping(path = ["/elo/player"])
  fun userByEloRank() : List<PlayerElo> = playerRankingByElo.get()
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByEloHandler : Supplier<List<PlayerElo>> {
  companion object : KLogging()

  private val ranking = mutableMapOf<UserName, Elo>()
  private val cache = AtomicReference<List<PlayerElo>>(listOf())

  @EventHandler
  fun on(e: PlayerRankingChanged) {
    logger.info { "Player '${e.player}' new rating is '${e.eloRanking}'" }
    update(PlayerElo(e.player.value , e.eloRanking))
  }

  @EventHandler
  fun on(e: PlayerCreated) {
    logger.info { "Player '${e.userName}' initial rating is '${e.initialElo}'" }
    update(PlayerElo(e.userName.value, e.initialElo))
  }

  private fun update(playerElo: PlayerElo) {
    ranking.put(playerElo.userName, playerElo.elo)
    cache.set(ranking.map { it.toPair() }.map { PlayerElo(it.first, it.second) }.sorted())

    logger.debug { "new elo ranking for player: $cache" }
  }

  override fun get() = cache.get()

}

data class PlayerElo(val userName: UserName, val elo: Elo) : Comparable<PlayerElo>  {

  constructor(userName: String, elo: Elo) : this(UserName(userName), elo)

  override fun compareTo(other: PlayerElo): Int = other.elo.compareTo(elo)
}
