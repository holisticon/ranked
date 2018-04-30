@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.TeamColor
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * The rest controller provides getters for key figures based on the played sets
 */
@RestController
@RequestMapping(value = ["/view/sets"])
class PlayerRankingBySetsView(
    private val playerRankingBySets: PlayerRankingBySets
) {
  @GetMapping(path = ["/count"])
  fun userListByGoalSum(): Set<PlayerSetCount> = playerRankingBySets.getSetCount()

  /*@GetMapping(path = ["/player/{userName}"])
  fun goalStatsByPlayer(@PathVariable("userName") userName: String): PlayerGoalStats = playerRankingByGoals.getGoalStatsForPlayer(userName)*/
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingBySets {

  private val setCount = mutableMapOf<UserName, PlayerSetCount>()

  @EventHandler
  fun on(e: MatchCreated) {
    e.matchSets.forEach {
      if (it.winner() === TeamColor.RED) {
        incWonSetsForPlayersOfTeam(e.teamRed, it.offenseRed)
        incLostSetsForPlayersOfTeam(e.teamBlue, it.offenseBlue)
      } else {
        incWonSetsForPlayersOfTeam(e.teamBlue, it.offenseBlue)
        incLostSetsForPlayersOfTeam(e.teamRed, it.offenseRed)
      }
    }
  }

  private fun incWonSetsForPlayersOfTeam(team: Team, offense: UserName) {
    setCount.putIfAbsent(team.player1, PlayerSetCount(team.player1, 0, 0, 0, 0))
    setCount.putIfAbsent(team.player2, PlayerSetCount(team.player2, 0, 0, 0, 0))

    if (team.player1 == offense) {
      setCount[team.player1]!!.incWonSetsInOffense()
      setCount[team.player2]!!.incWonSetsInDefense()
    } else if (team.player2 == offense) {
      setCount[team.player1]!!.incWonSetsInDefense()
      setCount[team.player2]!!.incWonSetsInOffense()
    }
  }

  private fun incLostSetsForPlayersOfTeam(team: Team, offense: UserName) {
    setCount.putIfAbsent(team.player1, PlayerSetCount(team.player1, 0, 0, 0, 0))
    setCount.putIfAbsent(team.player2, PlayerSetCount(team.player2, 0, 0, 0, 0))

    if (team.player1 == offense) {
      setCount[team.player1]!!.incLostSetsInOffense()
      setCount[team.player2]!!.incLostSetsInDefense()
    } else if (team.player2 == offense) {
      setCount[team.player1]!!.incLostSetsInDefense()
      setCount[team.player2]!!.incLostSetsInOffense()
    }
  }

  fun getSetCount() = setCount.values.toSet()
}

data class PlayerSetCount(val userName: UserName, val wonSets: InPosition<Int>, val lostSets: InPosition<Int>) {

  constructor(userName: UserName, wonSetsInOffense: Int, wonSetsInDefense: Int,
              lostSetsInOffense: Int, lostSetsInDefense: Int) :
      this(userName,
          InPosition(wonSetsInOffense, wonSetsInDefense),
          InPosition(lostSetsInOffense, lostSetsInDefense))

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