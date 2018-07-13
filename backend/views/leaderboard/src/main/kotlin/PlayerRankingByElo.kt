@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Elo
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerRankingChanged
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

const val PROCESSING_GROUP = "PlayerLeaderBoard"

/**
 * The rest controller just provides a facade for [PlayerRankingByEloHandler.get].
 */
@RestController
@RequestMapping(value = ["/view/elo"])
class PlayerLeaderBoardView(
  private val playerRankingByElo: PlayerRankingByEloHandler
) {
  @GetMapping(path = ["/player"])
  fun userListByEloRank() : List<PlayerElo> = playerRankingByElo.get()

  @GetMapping(path = ["/player/{userName}"])
  fun eloHistoryByPlayer(@PathVariable("userName") userName: String): MutableList<Pair<LocalDateTime, Elo>> = playerRankingByElo.getHistory(userName)



}

/**
 * The business logic for elo-rankings, handles axon events that affect players and elo and provides a sorted list
 * descending by elo value.
 */
@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByEloHandler : Supplier<List<PlayerElo>> {
  companion object : KLogging()

  /**
   * Holds a mutable list of userName->elo, this is the data store that is updated with every elo change.
   */
  private val ranking = mutableMapOf<UserName, Elo>()

  /**
   * Holds a mutable list of userName->List<timestamp, elo>, this is the history of elo values that is updated with every elo change.
   */
  private val rankingHistory = mutableMapOf<UserName, MutableList<Pair<LocalDateTime, Elo>>>()

  /**
   * The cache holds an immutable list of [PlayerElo] for the json return. It is sorted descending by elo value.
   * A cache is used because we only need to sort once per update and can return the same immutable sorted list
   * for every get request after that.
   */
  private val cache = AtomicReference<List<PlayerElo>>(listOf())

  @EventHandler
  fun on(e: PlayerRankingChanged, @Timestamp t: Instant) {
    logger.debug { "Player '${e.player}' new rating is '${e.eloRanking}'" }
    ranking[e.player] = e.eloRanking

    rankingHistory.putIfAbsent(e.player, mutableListOf())
    rankingHistory[e.player]!!.add(Pair(LocalDateTime.ofInstant(t, ZoneId.of("Europe/Berlin")), e.eloRanking))

    cache.set(ranking.map { it.toPair() }.map { PlayerElo(it.first, it.second) }.sorted())
  }

  override fun get() = cache.get()!!

  fun getHistory(userName: String) = rankingHistory[UserName(userName)] ?: mutableListOf()
}

/**
 * The return value for the player ranking provides a username (string) and an elo value (int).
 */
data class PlayerElo(val userName: UserName, val elo: Elo) : Comparable<PlayerElo>  {

  constructor(userName: String, elo: Elo) : this(UserName(userName), elo)

  override fun compareTo(other: PlayerElo): Int = other.elo.compareTo(elo)
}
