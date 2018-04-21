import de.holisticon.ranked.model.*
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.view.leaderboard.TeamRankingByGoals
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.within
import org.assertj.core.api.Assertions.withinPercentage
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class TeamStatsSpec {

  private val ranking = TeamRankingByGoals()

  private val teamHolis = Team(UserName("O3"), UserName("Timo"))
  private val teamMuppets = Team(UserName("fozzy"), UserName("gonzo"))
  private val teamMarvel = Team(UserName("ironman"), UserName("hulk"))

  @Before
  fun setUp() {
    // for each test, make sure no players are present
    Assertions.assertThat(ranking.matches).isEmpty()
    Assertions.assertThat(ranking.teamStats).isEmpty()
    val teamCreatedEvent1 = TeamCreated("1", teamHolis, "Holis")
    val teamCreatedEvent2 = TeamCreated("2", teamMuppets, "Muppets")
    val teamCreatedEvent3 = TeamCreated("3", teamMarvel, "Marvels")

    ranking.on(teamCreatedEvent1)
    ranking.on(teamCreatedEvent2)
    ranking.on(teamCreatedEvent3)

    Assertions.assertThat(ranking.teamStats.contains(teamHolis))
    Assertions.assertThat(ranking.teamStats.contains(teamMuppets))
    Assertions.assertThat(ranking.teamStats.contains(teamMarvel))

    Assertions.assertThat(ranking.teamStats[teamHolis]!!.name).isEqualTo("Holis")
    Assertions.assertThat(ranking.teamStats[teamMuppets]!!.name).isEqualTo("Muppets")
    Assertions.assertThat(ranking.teamStats[teamMarvel]!!.name).isEqualTo("Marvels")
  }



  @Test
  fun `a new match with times is added`() {
    val matchSets = mutableListOf<AbstractMatchSet>()
    val matchStartTime = LocalDateTime.of(2018, 1, 1, 12, 0, 0)

    matchSets.add(TimedMatchSet(
      listOf(
        Pair(TeamColor.RED, matchStartTime.plusSeconds(8)), // 8s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(40)), // 32s
        Pair(TeamColor.BLUE, matchStartTime.plusSeconds(45)), // 5s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(63)), // 18s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(66)), // 3s
        Pair(TeamColor.BLUE, matchStartTime.plusSeconds(76)), // 10s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(89)), // 13s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(99)) // 10s
      ), teamHolis.player1, teamMuppets.player1
    ))
    matchSets.add(TimedMatchSet(
      listOf(
        Pair(TeamColor.BLUE, matchStartTime.plusSeconds(100)), // 1s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(115)), // 15s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(125)), // 10s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(143)), // 18s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(155)), // 12s
        Pair(TeamColor.BLUE, matchStartTime.plusSeconds(169)), // 14s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(195)), // 26s
        Pair(TeamColor.RED, matchStartTime.plusSeconds(212)) // 17s
      ), teamHolis.player2, teamMuppets.player2
    ))

    ranking.on(MatchCreated("abc", teamHolis, teamMuppets, matchSets, matchStartTime, null))
    ranking.on(TeamWonMatch("abc", teamHolis, teamMuppets))

    val teamStats = ranking.teamStats


    val teamHolisGoalTime = teamStats.get(teamHolis)!!.avgGoalTime
    val teamHolisGoalsScoredCount = teamStats.get(teamHolis)!!.goalsScored
    val teamHolisMatchesWon = teamStats.get(teamHolis)!!.matchesWon

    val teamMuppetsGoalTime = teamStats.get(teamMuppets)!!.avgGoalTime
    val teamMuppetsGoalsScoredCount = teamStats.get(teamMuppets)!!.goalsScored
    val teamMuppetsMatchesWon = teamStats.get(teamMuppets)!!.matchesWon

    Assertions.assertThat(teamHolisGoalTime).isCloseTo((15.166667), withinPercentage(0.1))
    Assertions.assertThat(teamHolisGoalsScoredCount).isEqualTo(12)
    Assertions.assertThat(teamHolisMatchesWon).isEqualTo(1)
    Assertions.assertThat(teamMuppetsGoalTime).isCloseTo((7.5), withinPercentage(0.1))
    Assertions.assertThat(teamMuppetsGoalsScoredCount).isEqualTo(4)
    Assertions.assertThat(teamMuppetsMatchesWon).isEqualTo(0)

  }

}
