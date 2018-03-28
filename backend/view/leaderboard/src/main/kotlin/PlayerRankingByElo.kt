@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Elo
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerRankingChanged
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

const val PROCESSING_GROUP = "PlayerLeaderBoard"

/**
 * The rest controller just provides a facade for [PlayerRankingByEloHandler.get].
 */
@RestController
@RequestMapping(value = ["/view"])
class PlayerLeaderBoardView(
  private val playerRankingByElo: PlayerRankingByEloHandler,
  private val playerRankingByGoals: PlayerRankingByGoals
) {
  @GetMapping(path = ["/elo/player"])
  fun userListByEloRank() : List<PlayerElo> = playerRankingByElo.get()

  @GetMapping(path = ["/goals/sum"])
  fun userListByGoalSum() : List<PlayerGoalSum> = playerRankingByGoals.getGoalSum()

  @GetMapping(path = ["/goals/difference"])
  fun userListByGoalDifference() : List<PlayerGoalDifference> = playerRankingByGoals.getGoalDifference()

  @GetMapping(path = ["/goals/player/{userName}"])
  fun goalStatsByPlayer(@PathVariable("userName") userName: String) : PlayerGoalStats = playerRankingByGoals.getGoalStatsForPlayer(userName)

  @GetMapping(path = ["/goals/time/average"])
  fun userListByGoalTimeAverage() : List<PlayerGoalTimeAverage> = playerRankingByGoals.getGoalTimeAverage()
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
   * The cache holds an immutable list of [PlayerElo] for the json return. It is sorted descending by elo value.
   * A cache is used because we only need to sort once per update and can return the same immutable sorted list
   * for every get request after that.
   */
  private val cache = AtomicReference<List<PlayerElo>>(listOf())

  @EventHandler
  fun on(e: PlayerRankingChanged) {
    logger.debug { "Player '${e.player}' new rating is '${e.eloRanking}'" }
    ranking[e.player] = e.eloRanking
    cache.set(ranking.map { it.toPair() }.map { PlayerElo(it.first, it.second) }.sorted())
  }


  /**
   * Update the elo rankings of the players.
   * @param playerElo new player elo
   */
  private fun update(playerElo: PlayerElo, change: Boolean = true) {

  }

  override fun get() = cache.get()!!
}

/**
 * The return value for the player ranking provides a username (string) and an elo value (int).
 */
data class PlayerElo(val userName: UserName, val elo: Elo) : Comparable<PlayerElo>  {

  constructor(userName: String, elo: Elo) : this(UserName(userName), elo)

  override fun compareTo(other: PlayerElo): Int = other.elo.compareTo(elo)
}
