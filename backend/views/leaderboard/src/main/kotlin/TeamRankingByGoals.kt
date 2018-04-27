@file:Suppress("UNUSED")

package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.TeamColor
import de.holisticon.ranked.model.TimedMatchSet
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.model.event.TeamWonMatchSet
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.temporal.ChronoUnit


/**
 * The rest controller provides getters for key figures based on team stats
 */
@RestController
@RequestMapping(value = ["/view/team/stats"])
class TeamRankingByGoalsView(private val teamRankingByGoals: TeamRankingByGoals) {
  @GetMapping
  fun teamStats(): Set<TeamStats> = teamRankingByGoals.teamStats
                                      .map { it.value }
                                      .filter{ (it.matchesLost + it.matchesWon) > 0}
                                      .sortedBy { it.name }
                                      .toSet()
}


@ProcessingGroup("Team")
@Component
class TeamRankingByGoals() {

  val teamStats: MutableMap<Team, TeamStats> = mutableMapOf()
  val matches: MutableMap<String, MatchCreated> = mutableMapOf()

  @EventHandler
  fun on(e: MatchCreated) {
    matches[e.matchId] = e
  }

  @EventHandler
  fun on(e: TeamWonMatch) {
    val match = matches[e.matchId] ?: throw IllegalStateException("Match won without being created.")
    val winner = e.team
    val looser = e.looser

    teamStats[winner]?.matchesWon = teamStats[winner]?.matchesWon!!.inc()
    teamStats[looser]?.matchesLost = teamStats[looser]?.matchesLost!!.inc()

    var goalsBlue = 0
    var goalsRed = 0
    var lastGoalTime = match.startTime

    match.matchSets.forEach {
      goalsBlue += it.goalsBlue
      goalsRed += it.goalsRed

      if (it is TimedMatchSet) {
        var goalTime: Long
        it.goals.forEach {
          goalTime = ChronoUnit.SECONDS.between(lastGoalTime.toLocalTime(), it.second.toLocalTime())

          if (goalTime > 0) {
            if (it.first == TeamColor.RED) {
              teamStats[match.teamRed]!!.totalGoalTime += goalTime
            } else {
              teamStats[match.teamBlue]!!.totalGoalTime += goalTime
            }
          }

          lastGoalTime = it.second
        }
      }
    }

    teamStats[match.teamBlue]?.goalsScored = teamStats[match.teamBlue]?.goalsScored!! + goalsBlue
    teamStats[match.teamBlue]?.goalsConceded = teamStats[match.teamBlue]?.goalsConceded!! + goalsRed

    teamStats[match.teamRed]?.goalsScored = teamStats[match.teamRed]?.goalsScored!! + goalsRed
    teamStats[match.teamRed]?.goalsConceded = teamStats[match.teamRed]?.goalsConceded!! + goalsBlue

    teamStats[match.teamRed]!!.calcAvgGoalTime()
    teamStats[match.teamBlue]!!.calcAvgGoalTime()

  }

  @EventHandler
  fun on(e: TeamWonMatchSet) {
    teamStats[e.team]?.setsWon = teamStats[e.team]?.setsWon!! + 1
    teamStats[e.looser]?.setsLost = teamStats[e.looser]?.setsLost!! + 1
  }

  @EventHandler
  fun on(e: TeamCreated) {
    teamStats[e.team] = TeamStats(e.name)
  }
}

class TeamStats(var name: String,
                var goalsScored: Int = 0,
                var goalsConceded: Int = 0,
                var setsWon: Int = 0,
                var setsLost: Int = 0,
                var matchesWon: Int = 0,
                var matchesLost: Int = 0,
                var totalGoalTime: Double = 0.0,
                var avgGoalTime: Double = 0.0) {

  fun calcAvgGoalTime() {
    this.avgGoalTime = if (goalsScored > 0) {
      totalGoalTime / goalsScored
    } else {
      0.0
    }
  }
}
