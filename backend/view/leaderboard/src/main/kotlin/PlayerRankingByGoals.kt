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
import java.util.concurrent.atomic.AtomicReference

/**
 * The rest controller provides getters for key figures based on the player goals.
 */
@RestController
@RequestMapping(value = ["/view/goals"])
class PlayerRankingByGoalsView(
    private val playerRankingByGoals: PlayerRankingByGoals
) {
  @GetMapping(path = ["/count"])
  fun userListByGoalSum(): Set<PlayerGoalCount> = playerRankingByGoals.getGoalCount()

  @GetMapping(path = ["/player/{userName}"])
  fun goalStatsByPlayer(@PathVariable("userName") userName: String): PlayerGoalStats = playerRankingByGoals.getGoalStatsForPlayer(userName)

  @GetMapping(path = ["/time/average"])
  fun userListByGoalTimeAverage(): List<PlayerGoalTimeAverage> = playerRankingByGoals.getGoalTimeAverage()
}

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByGoals {

  private val goalCount = mutableMapOf<UserName, PlayerGoalCount>()
  private val goalTimeAverage = mutableMapOf<UserName, Int>()

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

      addScoredGoalsToPlayersOfTeam(e.teamRed, it.offenseRed, it.goalsRed)
      addScoredGoalsToPlayersOfTeam(e.teamBlue, it.offenseBlue, it.goalsBlue)

      addConcededGoalsToPlayersOfTeam(e.teamRed, it.offenseRed, it.goalsBlue)
      addConcededGoalsToPlayersOfTeam(e.teamBlue, it.offenseBlue, it.goalsRed)

      if (it is TimedMatchSet) {
        var goalTime: Long
        it.goals.forEach {
          goalTime = ChronoUnit.SECONDS.between(lastGoalTime.toLocalTime(), it.second.toLocalTime())

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

    goalTimeAverageCache.set(goalTimeAverage.map { it.toPair() }.map { PlayerGoalTimeAverage(it.first, it.second) }.sorted())
  }

  private fun addScoredGoalsToPlayersOfTeam(team: Team, offense: UserName, goals: Int) {
    goalCount.putIfAbsent(team.player1, PlayerGoalCount(team.player1, 0, 0, 0, 0))
    goalCount.putIfAbsent(team.player2, PlayerGoalCount(team.player2, 0, 0, 0, 0))

    if (team.player1 == offense) {
      goalCount[team.player1]!!.addGoalsScoredInOffense(goals)
      goalCount[team.player2]!!.addGoalsScoredInDefense(goals)
    } else if (team.player2 == offense) {
      goalCount[team.player1]!!.addGoalsScoredInDefense(goals)
      goalCount[team.player2]!!.addGoalsScoredInOffense(goals)
    }
  }

  private fun addConcededGoalsToPlayersOfTeam(team: Team, offense: UserName, goals: Int) {
    goalCount.putIfAbsent(team.player1, PlayerGoalCount(team.player1, 0, 0, 0, 0))
    goalCount.putIfAbsent(team.player2, PlayerGoalCount(team.player2, 0, 0, 0, 0))

    if (team.player1 == offense) {
      goalCount[team.player1]!!.addGoalsConcededInOffense(goals)
      goalCount[team.player2]!!.addGoalsConcededInDefense(goals)
    } else if (team.player2 == offense) {
      goalCount[team.player1]!!.addGoalsConcededInDefense(goals)
      goalCount[team.player2]!!.addGoalsConcededInOffense(goals)
    }
  }

  private fun getGoalSumOfPlayer(player: UserName) = goalCount[player]?.goalsScored?.let { it.whenInOffense + it.whenInDefense }

  private fun addAverageGoalTimeToPlayersOfTeam(team: Team, goalTimeSum: Long, goalCount: Int) {
    addAverageGoalTimeToPlayer(team.player1, goalTimeSum, goalCount)
    addAverageGoalTimeToPlayer(team.player2, goalTimeSum, goalCount)
  }

  private fun addAverageGoalTimeToPlayer(player: UserName, goalTimeSum: Long, goalCount: Int) {
    if (goalTimeAverage[player] == null) {
      goalTimeAverage[player] = (goalTimeSum / goalCount).toInt()
    } else {
      val previousGoalTimeSum = goalTimeAverage[player]!! * (getGoalSumOfPlayer(player)!! - goalCount)
      goalTimeAverage[player] = ((previousGoalTimeSum + goalTimeSum) / getGoalSumOfPlayer(player)!!).toInt()
    }
  }

  fun getGoalCount() = goalCount.values.toSet()
  fun getGoalTimeAverage() = goalTimeAverageCache.get()!!
  fun getGoalStatsForPlayer(userName: String) =
      UserName(userName).let { PlayerGoalStats(goalTimeAverage[it], goalCount[it]?.goalsScored, goalCount[it]?.goalsConceded) }
}

data class GoalCount(var whenInOffense: Int, var whenInDefense: Int)

/**
 * Return value for the total goal count provides a username (string) and the sum of goals (int).
 */
data class PlayerGoalCount(val userName: UserName, val goalsScored: GoalCount, val goalsConceded: GoalCount) {

  constructor(userName: UserName, goalsScoredInOffense: Int, goalsScoredInDefense: Int,
              goalsConcededInOffense: Int, goalsConcededInDefense: Int) :
      this(userName,
          GoalCount(goalsScoredInOffense, goalsScoredInDefense),
          GoalCount(goalsConcededInOffense, goalsConcededInDefense))

  fun addGoalsScoredInOffense(goals: Int) {
    goalsScored.whenInOffense += goals;
  }

  fun addGoalsScoredInDefense(goals: Int) {
    goalsScored.whenInDefense += goals;
  }

  fun addGoalsConcededInOffense(goals: Int) {
    goalsConceded.whenInOffense += goals;
  }

  fun addGoalsConcededInDefense(goals: Int) {
    goalsConceded.whenInDefense += goals;
  }
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
data class PlayerGoalStats(val goalTimeAverage: Int?, val goalsScored: GoalCount?, val goalsConceded: GoalCount?)
