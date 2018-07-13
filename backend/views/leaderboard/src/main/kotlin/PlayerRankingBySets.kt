@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.TeamColor
import de.holisticon.ranked.model.TimedMatchSet
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.temporal.ChronoUnit

/**
 * The rest controller provides getters for key figures based on the played sets
 */
@RestController
@RequestMapping(value = ["/view/sets"])
class PlayerRankingBySetsView(
    private val playerRankingBySets: PlayerRankingBySets
) {
  @GetMapping(path = ["/player/{userName}"])
  fun setStatsByPlayer(@PathVariable("userName") userName: String): PlayerSetStats? = playerRankingBySets.getSetStatsForPlayer(userName)
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingBySets {

  private val setStats = mutableMapOf<UserName, PlayerSetStats>()

  @EventHandler
  fun on(e: MatchCreated) {
    ensurePlayerStatExists(e.teamBlue);
    ensurePlayerStatExists(e.teamRed);

    var lastSetEndTime = e.startTime.toLocalTime();

    e.matchSets.forEach {
      if (it is TimedMatchSet) {
        val endTime = it.goals.last().second.toLocalTime()
        val setTime = ChronoUnit.SECONDS.between(lastSetEndTime, endTime)
        setAvgSetTimeForPlayersOfTeam(e.teamRed, setTime)
        setAvgSetTimeForPlayersOfTeam(e.teamBlue, setTime)
        lastSetEndTime = endTime
      }

      if (it.winner() === TeamColor.RED) {
        incWonSetsForPlayersOfTeam(e.teamRed, it.offenseRed)
        incLostSetsForPlayersOfTeam(e.teamBlue, it.offenseBlue)
      } else {
        incWonSetsForPlayersOfTeam(e.teamBlue, it.offenseBlue)
        incLostSetsForPlayersOfTeam(e.teamRed, it.offenseRed)
      }
    }
  }

  private fun ensurePlayerStatExists(team: Team) {
    setStats.putIfAbsent(team.player1, PlayerSetStats(team.player1, 0, 0, 0, 0, 0.0))
    setStats.putIfAbsent(team.player2, PlayerSetStats(team.player2, 0, 0, 0, 0, 0.0))
  }

  private fun incWonSetsForPlayersOfTeam(team: Team, offense: UserName) {
    if (team.player1 == offense) {
      setStats[team.player1]!!.incWonSetsInOffense()
      setStats[team.player2]!!.incWonSetsInDefense()
    } else if (team.player2 == offense) {
      setStats[team.player1]!!.incWonSetsInDefense()
      setStats[team.player2]!!.incWonSetsInOffense()
    }
  }

  private fun incLostSetsForPlayersOfTeam(team: Team, offense: UserName) {
    if (team.player1 == offense) {
      setStats[team.player1]!!.incLostSetsInOffense()
      setStats[team.player2]!!.incLostSetsInDefense()
    } else if (team.player2 == offense) {
      setStats[team.player1]!!.incLostSetsInDefense()
      setStats[team.player2]!!.incLostSetsInOffense()
    }
  }

  private fun setAvgSetTimeForPlayersOfTeam(team: Team, setTime: Long) {
    setAvgSetTimeForPlayer(team.player1, setTime);
    setAvgSetTimeForPlayer(team.player2, setTime);
  }

  private fun setAvgSetTimeForPlayer(player: UserName, setTime: Long) {
    val stats = setStats[player]!!;
    val totalSets = stats.lostSets.whenInOffense + stats.lostSets.whenInDefense + stats.wonSets.whenInOffense + stats.wonSets.whenInDefense;
    stats.averageMatchTime = calcAvgSetTime(totalSets, stats.averageMatchTime, setTime);
  }

  private fun calcAvgSetTime(setCount: Int, averageSetTime: Double, setTime: Long): Double {
    return (averageSetTime * setCount + setTime) / (setCount + 1);
  }

  fun getSetStatsForPlayer(playerName: String) = setStats[UserName(playerName)]
}

data class PlayerSetStats(val userName: UserName, val wonSets: InPosition<Int>, val lostSets: InPosition<Int>, var averageMatchTime: Double) {

  constructor(userName: UserName, wonSetsInOffense: Int, wonSetsInDefense: Int,
              lostSetsInOffense: Int, lostSetsInDefense: Int, averageMatchTime: Double) :
      this(userName,
          InPosition(wonSetsInOffense, wonSetsInDefense),
          InPosition(lostSetsInOffense, lostSetsInDefense),
          averageMatchTime)

  fun incWonSetsInOffense() {
    wonSets.whenInOffense++
  }

  fun incWonSetsInDefense() {
    wonSets.whenInDefense++
  }

  fun incLostSetsInOffense() {
    lostSets.whenInOffense++
  }

  fun incLostSetsInDefense() {
    lostSets.whenInDefense++
  }
}
