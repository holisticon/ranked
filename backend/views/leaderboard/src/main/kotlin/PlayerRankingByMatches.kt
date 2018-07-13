@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.TimedMatchSet
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
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
@RequestMapping(value = ["/view/matches"])
class PlayerRankingByMatchesView(
    private val playerRankingByMatches: PlayerRankingByMatches
) {
  @GetMapping(path = ["/player/{userName}"])
  fun matchStatsByPlayer(@PathVariable("userName") userName: String): PlayerMatchStats = playerRankingByMatches.getMatchStatsForPlayer(userName)
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByMatches {

  private val matchStats = mutableMapOf<UserName, PlayerMatchStats>()
  private val matches: MutableMap<String, MatchCreated> = mutableMapOf()

  @EventHandler
  fun on(e: MatchCreated) {
    matches[e.matchId] = e
  }

  @EventHandler
  fun on(e: TeamWonMatch) {
    val match = matches[e.matchId] ?: throw IllegalStateException("Match won without being created.")
    ensurePlayerStatExists(e.team)
    ensurePlayerStatExists(e.looser)

    val lastSet = match.matchSets.last();
    if (lastSet is TimedMatchSet) {
      val matchTime = ChronoUnit.SECONDS.between(match.startTime.toLocalTime(), lastSet.goals.last().second.toLocalTime())
      setAvgMatchTimeForPlayersOfTeam(e.team, matchTime)
      setAvgMatchTimeForPlayersOfTeam(e.looser, matchTime)
    }

    incWonMatchesForPlayersOfTeam(e.team)
    incLostMatchesForPlayersOfTeam(e.looser)
  }

  private fun ensurePlayerStatExists(team: Team) {
    matchStats.putIfAbsent(team.player1, PlayerMatchStats(team.player1, 0, 0, 0.0))
    matchStats.putIfAbsent(team.player2, PlayerMatchStats(team.player2, 0, 0, 0.0))
  }

  private fun setAvgMatchTimeForPlayersOfTeam(team: Team, matchTime: Long) {
    setAvgMatchTimeForPlayer(team.player1, matchTime);
    setAvgMatchTimeForPlayer(team.player2, matchTime);
  }

  private fun setAvgMatchTimeForPlayer(player: UserName, matchTime: Long) {
    val stats = matchStats[player]!!;
    stats.averageMatchTime = calcAvgMatchTime(stats.lostMatches + stats.wonMatches, stats.averageMatchTime, matchTime);
  }

  private fun calcAvgMatchTime(matchCount: Int, averageMatchTime: Double, matchTime: Long): Double {
    return (averageMatchTime * matchCount + matchTime) / (matchCount + 1);
  }

  private fun incWonMatchesForPlayersOfTeam(team: Team) {
    matchStats[team.player1]!!.wonMatches++
    matchStats[team.player2]!!.wonMatches++
  }

  private fun incLostMatchesForPlayersOfTeam(team: Team) {
    matchStats[team.player1]!!.lostMatches++
    matchStats[team.player2]!!.lostMatches++
  }

  fun getMatchStatsForPlayer(playerName: String) = matchStats[UserName(playerName)] ?: PlayerMatchStats(UserName(playerName), 0, 0, 0.0)
}

data class PlayerMatchStats(val userName: UserName, var wonMatches: Int, var lostMatches: Int, var averageMatchTime: Double)
