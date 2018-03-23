package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReference

@Component
@ProcessingGroup(PROCESSING_GROUP)
class PlayerRankingByGoals {

  private val goalSum = mutableMapOf<UserName, Int>()
  private val goalDifference = mutableMapOf<UserName, Int>()

  private val goalSumCache = AtomicReference<List<PlayerGoalSum>>(listOf())
  private val goalDifferenceCache = AtomicReference<List<PlayerGoalDifference>>(listOf())

  @EventHandler
  fun on(e: MatchCreated) {
    var goalsRed = 0
    var goalsBlue = 0

    e.matchSets.forEach {
      goalsRed += it.goalsRed
      goalsBlue += it.goalsBlue
    }

    addGoalSumToPlayersOfTeam(e.teamRed, goalsRed)
    addGoalSumToPlayersOfTeam(e.teamBlue, goalsBlue)

    addGoalDifferenceToPlayersOfTeam(e.teamRed, goalsRed - goalsBlue)
    addGoalDifferenceToPlayersOfTeam(e.teamBlue, goalsBlue - goalsRed)

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

  fun getGoalSum() = goalSumCache.get()!!
  fun getGoalDifference() = goalDifferenceCache.get()!!
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
 * Carries
 */
data class PlayerGoalStats(val goalSum: Int?, val goalDifference: Int?)
