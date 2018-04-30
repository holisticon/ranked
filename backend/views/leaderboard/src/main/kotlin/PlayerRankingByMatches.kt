@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.TeamWonMatch
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
@RequestMapping(value = ["/view/matches"])
class PlayerRankingByMatchesView(
    private val playerRankingByMatches: PlayerRankingByMatches
) {
  @GetMapping(path = ["/count"])
  fun userListByGoalSum(): Set<PlayerMatchCount> = playerRankingByMatches.getMatchCount()
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByMatches {

  private val matchCount = mutableMapOf<UserName, PlayerMatchCount>()

  @EventHandler
  fun on(e: TeamWonMatch) {
    incWonMatchesForPlayersOfTeam(e.team)
    incLostMatchesForPlayersOfTeam(e.looser)
  }

  private fun incWonMatchesForPlayersOfTeam(team: Team) {
    matchCount.putIfAbsent(team.player1, PlayerMatchCount(team.player1, 0, 0))
    matchCount.putIfAbsent(team.player2, PlayerMatchCount(team.player2, 0, 0))

    matchCount[team.player1]!!.wonMatches++
    matchCount[team.player2]!!.wonMatches++
  }

  private fun incLostMatchesForPlayersOfTeam(team: Team) {
    matchCount.putIfAbsent(team.player1, PlayerMatchCount(team.player1, 0, 0))
    matchCount.putIfAbsent(team.player2, PlayerMatchCount(team.player2, 0, 0))

    matchCount[team.player1]!!.lostMatches++
    matchCount[team.player2]!!.lostMatches++
  }

  fun getMatchCount() = matchCount.values.toSet()
}

data class PlayerMatchCount(val userName: UserName, var wonMatches: Int, var lostMatches: Int)
