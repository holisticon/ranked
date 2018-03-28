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
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicReference

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByGoals {

  private val goalSum = mutableMapOf<UserName, Int>()
  private val goalDifference = mutableMapOf<UserName, Int>()
  private val goalTimeAverage = mutableMapOf<UserName, Int>()

  private val goalSumCache = AtomicReference<List<PlayerGoalSum>>(listOf())
  private val goalDifferenceCache = AtomicReference<List<PlayerGoalDifference>>(listOf())
  private val goalTimeAverageCache = AtomicReference<List<PlayerGoalTimeAverage>>(listOf())

  @EventHandler
  fun on(e: MatchCreated) {
    var goalsRed = 0
    var goalsBlue = 0
    var goalTimeSumRed = 0L
    var goalTimeSumBlue = 0L
    var lastGoalTime = e.startTime

    e.matchSets.forEach {
      goalsRed += it.goalsRed
      goalsBlue += it.goalsBlue

      if (it is TimedMatchSet) {
        var goalTime: Long
        it.goals.forEach {
          goalTime = ChronoUnit.SECONDS.between(it.second.toLocalTime(), lastGoalTime.toLocalTime())

          if (it.first == TeamColor.RED) {
            goalTimeSumRed += goalTime
          } else {
            goalTimeSumBlue += goalTime
          }

          lastGoalTime = it.second;
        }
      }
    }

    addAverageGoalTimeToPlayersOfTeam(e.teamRed, goalTimeSumRed, goalsRed)
    addAverageGoalTimeToPlayersOfTeam(e.teamBlue, goalTimeSumBlue, goalsBlue)

    addGoalSumToPlayersOfTeam(e.teamRed, goalsRed)
    addGoalSumToPlayersOfTeam(e.teamBlue, goalsBlue)

    addGoalDifferenceToPlayersOfTeam(e.teamRed, goalsRed - goalsBlue)
    addGoalDifferenceToPlayersOfTeam(e.teamBlue, goalsBlue - goalsRed)

    goalTimeAverageCache.set(goalTimeAverage.map { it.toPair() }.map { PlayerGoalTimeAverage(it.first, it.second) }.sorted())
    goalSumCache.set(goalSum.map { it.toPair() }.map { PlayerGoalSum(it.first, it.second) }.sorted())
    goalDifferenceCache.set(goalDifference.map { it.toPair() }.map { PlayerGoalDifference(it.first, it.second) }.sorted())
  }

  private fun addGoalSumToPlayersOfTeam(team: Team, increment: Int) {
    goalSum.putIfAbsent(team.player1, 0)
    goalSum.putIfAbsent(team.player2, 0)

    goalSum[team.player1] = increment + goalSum[team.player1]!!
    goalSum[team.player2] = increment + goalSum[team.player2]!!
  }

  private fun addGoalDifferenceToPlayersOfTeam(team: Team, increment: Int) {
    goalDifference.putIfAbsent(team.player1, 0)
    goalDifference.putIfAbsent(team.player2, 0)

    goalDifference[team.player1] = increment + goalDifference[team.player1]!!
    goalDifference[team.player2] = increment + goalDifference[team.player2]!!
  }

  private fun addAverageGoalTimeToPlayersOfTeam(team: Team, goalTimeSum: Long, goalCount: Int) {
    addAverageGoalTimeToPlayer(team.player1, goalTimeSum, goalCount)
    addAverageGoalTimeToPlayer(team.player2, goalTimeSum, goalCount)
  }

  private fun addAverageGoalTimeToPlayer(player: UserName, goalTimeSum: Long, goalCount: Int) {
    if (goalTimeAverage[player] == null) {
      goalTimeAverage[player] = (goalTimeSum / goalCount).toInt()
    } else {
      val previousGoalTimeSum = goalTimeAverage[player]!! * goalSum[player]!!
      goalTimeAverage[player] = ((previousGoalTimeSum + goalTimeSum) / (goalSum[player]!! + goalCount)).toInt()
    }
  }

  fun getGoalSum() = goalSumCache.get()!!
  fun getGoalDifference() = goalDifferenceCache.get()!!
  fun getGoalTimeAverage() = goalTimeAverageCache.get()!!
  fun getGoalStatsForPlayer(userName: String) = PlayerGoalStats(goalSum[UserName(userName)], goalDifference[UserName(userName)])
}

/**
 * Return value for the total goal count provides a username (string) and the sum of goals (int).
 */
data class PlayerGoalSum(val userName: UserName, val goals: Int) : Comparable<PlayerGoalSum> {

  constructor(userName: String, goals: Int) : this(UserName(userName), goals)

  override fun compareTo(other: PlayerGoalSum): Int = other.goals.compareTo(goals)
}

/**
 * Return value for the total goal difference provides a username (string) and the sum of goal differences (int).
 */
data class PlayerGoalDifference(val userName: UserName, val goalDifference: Int) : Comparable<PlayerGoalDifference> {

  constructor(userName: String, goalDifference: Int) : this(UserName(userName), goalDifference)

  override fun compareTo(other: PlayerGoalDifference): Int = other.goalDifference.compareTo(goalDifference)
}

/**
 * Return value for the goal time provides a username (string) and the time how long the player needs for a goal in seconds (int).
 */
data class PlayerGoalTimeAverage(val userName: UserName, val goalTime: Int) : Comparable<PlayerGoalTimeAverage> {

  constructor(userName: String, goalTime: Int) : this(UserName(userName), goalTime)

  override fun compareTo(other: PlayerGoalTimeAverage): Int = other.goalTime.compareTo(goalTime)
}

/**
 * Carries various stats for a player on goal level.
 */
data class PlayerGoalStats(val goalSum: Int?, val goalDifference: Int?)
