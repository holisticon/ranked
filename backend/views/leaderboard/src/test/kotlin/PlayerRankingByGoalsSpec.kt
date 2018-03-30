package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.*
import de.holisticon.ranked.model.event.MatchCreated
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class PlayerRankingByGoalsSpec {

  private val ranking = PlayerRankingByGoals()

  private val teamRed = Team(UserName("kermit"), UserName("piggy"))
  private val teamBlue = Team(UserName("fozzy"), UserName("gonzo"))
  private val teamGreen = Team(UserName("scooter"), UserName("beaker"))

  @Before
  fun setUp() {
    // for each test, make sure no players are present
    assertThat(ranking.getGoalCount()).isEmpty()
    assertThat(ranking.getGoalTimeAverage()).isEmpty()
  }

  @Test
  fun `a new match without times is added`() {
    val matchSets = mutableListOf<AbstractMatchSet>()
    val matchStartTime = LocalDateTime.of(2018, 1, 1, 12, 0, 0)

    matchSets.add(MatchSet(6, 2, teamRed.player1, teamBlue.player1))
    matchSets.add(MatchSet(4, 6, teamRed.player2, teamBlue.player2))
    matchSets.add(MatchSet(0, 6, teamRed.player1, teamBlue.player2))

    ranking.on(MatchCreated("abc", teamRed, teamBlue, matchSets, matchStartTime, null))

    val goalCount = ranking.getGoalCount()

    val teamRedPlayer1GoalCount = goalCount.find { it.userName == teamRed.player1 }!!
    val teamRedPlayer2GoalCount = goalCount.find { it.userName == teamRed.player2 }!!
    val teamBluePlayer1GoalCount = goalCount.find { it.userName == teamBlue.player1 }!!
    val teamBluePlayer2GoalCount = goalCount.find { it.userName == teamBlue.player2 }!!

    assertThat(teamRedPlayer1GoalCount.goalsScored.whenInOffense).isEqualTo(6)
    assertThat(teamRedPlayer2GoalCount.goalsScored.whenInOffense).isEqualTo(4)
    assertThat(teamBluePlayer1GoalCount.goalsScored.whenInOffense).isEqualTo(2)
    assertThat(teamBluePlayer2GoalCount.goalsScored.whenInOffense).isEqualTo(12)

    assertThat(teamRedPlayer1GoalCount.goalsScored.whenInDefense).isEqualTo(4)
    assertThat(teamRedPlayer2GoalCount.goalsScored.whenInDefense).isEqualTo(6)
    assertThat(teamBluePlayer1GoalCount.goalsScored.whenInDefense).isEqualTo(12)
    assertThat(teamBluePlayer2GoalCount.goalsScored.whenInDefense).isEqualTo(2)

    assertThat(teamRedPlayer1GoalCount.goalsConceded.whenInOffense).isEqualTo(8)
    assertThat(teamRedPlayer2GoalCount.goalsConceded.whenInOffense).isEqualTo(6)
    assertThat(teamBluePlayer1GoalCount.goalsConceded.whenInOffense).isEqualTo(6)
    assertThat(teamBluePlayer2GoalCount.goalsConceded.whenInOffense).isEqualTo(4)

    assertThat(teamRedPlayer1GoalCount.goalsConceded.whenInDefense).isEqualTo(6)
    assertThat(teamRedPlayer2GoalCount.goalsConceded.whenInDefense).isEqualTo(8)
    assertThat(teamBluePlayer1GoalCount.goalsConceded.whenInDefense).isEqualTo(4)
    assertThat(teamBluePlayer2GoalCount.goalsConceded.whenInDefense).isEqualTo(6)
  }

  @Test
  fun `player goal counts are updated`() {
    val match1Sets = mutableListOf<AbstractMatchSet>()
    val matchStartTime = LocalDateTime.of(2018, 1, 1, 12, 0, 0)

    match1Sets.add(MatchSet(6, 2, teamRed.player1, teamBlue.player1))
    match1Sets.add(MatchSet(4, 6, teamRed.player2, teamBlue.player2))
    match1Sets.add(MatchSet(0, 6, teamRed.player1, teamBlue.player2))

    ranking.on(MatchCreated("abc", teamRed, teamBlue, match1Sets, matchStartTime, null))

    val match2Sets = mutableListOf<AbstractMatchSet>()
    match2Sets.add(MatchSet(3, 6, teamRed.player1, teamGreen.player1))
    match2Sets.add(MatchSet(6, 5, teamRed.player2, teamGreen.player2))
    match2Sets.add(MatchSet(6, 1, teamRed.player2, teamGreen.player2))

    ranking.on(MatchCreated("xyz", teamRed, teamGreen, match2Sets, matchStartTime, null))

    val goalCount = ranking.getGoalCount()

    val teamRedPlayer1GoalCount = goalCount.find { it.userName == teamRed.player1 }!!
    val teamRedPlayer2GoalCount = goalCount.find { it.userName == teamRed.player2 }!!

    assertThat(teamRedPlayer1GoalCount.goalsScored.whenInOffense).isEqualTo(9)
    assertThat(teamRedPlayer2GoalCount.goalsScored.whenInOffense).isEqualTo(16)

    assertThat(teamRedPlayer1GoalCount.goalsScored.whenInDefense).isEqualTo(16)
    assertThat(teamRedPlayer2GoalCount.goalsScored.whenInDefense).isEqualTo(9)

    assertThat(teamRedPlayer1GoalCount.goalsConceded.whenInOffense).isEqualTo(14)
    assertThat(teamRedPlayer2GoalCount.goalsConceded.whenInOffense).isEqualTo(12)

    assertThat(teamRedPlayer1GoalCount.goalsConceded.whenInDefense).isEqualTo(12)
    assertThat(teamRedPlayer2GoalCount.goalsConceded.whenInDefense).isEqualTo(14)
  }

  @Test
  fun `a new match with times is added`() {
    val matchSets = mutableListOf<AbstractMatchSet>()
    val matchStartTime = LocalDateTime.of(2018, 1, 1, 12, 0, 0)

    matchSets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.RED, matchStartTime.plusSeconds(8)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(40)),
            Pair(TeamColor.BLUE, matchStartTime.plusSeconds(45)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(63)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(66)),
            Pair(TeamColor.BLUE, matchStartTime.plusSeconds(76)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(89)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(99))
        ), teamRed.player1, teamBlue.player1
    ))
    matchSets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.BLUE, matchStartTime.plusSeconds(100)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(115)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(125)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(143)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(155)),
            Pair(TeamColor.BLUE, matchStartTime.plusSeconds(169)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(195)),
            Pair(TeamColor.RED, matchStartTime.plusSeconds(212))
        ), teamRed.player2, teamBlue.player2
    ))

    ranking.on(MatchCreated("abc", teamRed, teamBlue, matchSets, matchStartTime, null))

    val goalTimeAverage = ranking.getGoalTimeAverage()

    val teamRedPlayer1GoalTime = goalTimeAverage.find { it.userName == teamRed.player1 }!!
    val teamRedPlayer2GoalTime = goalTimeAverage.find { it.userName == teamRed.player2 }!!
    val teamBluePlayer1GoalTime = goalTimeAverage.find { it.userName == teamBlue.player1 }!!
    val teamBluePlayer2GoalTime = goalTimeAverage.find { it.userName == teamBlue.player2 }!!

    assertThat(teamRedPlayer1GoalTime.goalTime).isEqualTo(teamRedPlayer2GoalTime.goalTime).isEqualTo(15)
    assertThat(teamBluePlayer1GoalTime.goalTime).isEqualTo(teamBluePlayer2GoalTime.goalTime).isEqualTo(7)
  }

  @Test
  fun `player goal times are updated`() {
    val match1Sets = mutableListOf<AbstractMatchSet>()
    val match1StartTime = LocalDateTime.of(2018, 1, 1, 12, 10, 0)

    match1Sets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.RED, match1StartTime.plusSeconds(8)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(40)),
            Pair(TeamColor.BLUE, match1StartTime.plusSeconds(45)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(63)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(66)),
            Pair(TeamColor.BLUE, match1StartTime.plusSeconds(76)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(89)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(99))
        ), teamRed.player1, teamBlue.player1
    ))
    match1Sets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.BLUE, match1StartTime.plusSeconds(100)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(115)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(125)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(143)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(155)),
            Pair(TeamColor.BLUE, match1StartTime.plusSeconds(169)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(195)),
            Pair(TeamColor.RED, match1StartTime.plusSeconds(212))
        ), teamRed.player2, teamBlue.player2
    ))

    ranking.on(MatchCreated("abc", teamRed, teamBlue, match1Sets, match1StartTime, null))

    val match2Sets = mutableListOf<AbstractMatchSet>()
    val match2StartTime = LocalDateTime.of(2018, 1, 1, 12, 20, 0)

    match2Sets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(12)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(22)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(30)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(62)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(79)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(84)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(97)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(106)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(150))
        ), teamRed.player1, teamGreen.player1
    ))
    match2Sets.add(TimedMatchSet(
        listOf(
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(163)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(175)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(182)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(205)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(213)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(220)),
            Pair(TeamColor.RED, match2StartTime.plusSeconds(271)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(276)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(296)),
            Pair(TeamColor.BLUE, match2StartTime.plusSeconds(312))
        ), teamRed.player2, teamGreen.player2
    ))

    ranking.on(MatchCreated("xyz", teamRed, teamGreen, match2Sets, match2StartTime, null))

    val goalTimeAverage = ranking.getGoalTimeAverage()

    val teamRedPlayer1GoalTime = goalTimeAverage.find { it.userName == teamRed.player1 }!!
    val teamRedPlayer2GoalTime = goalTimeAverage.find { it.userName == teamRed.player2 }!!

    assertThat(teamRedPlayer1GoalTime.goalTime).isEqualTo(teamRedPlayer2GoalTime.goalTime).isEqualTo(16)
  }

  /*@Test
  fun `a player ranking is updated`() {
    ranking.on(PlayerRankingChanged(UserName("kermit"), 1000))
    ranking.on(PlayerRankingChanged(UserName("kermit"), 2000))

    val userByEloRank = ranking.get()

    assertThat(userByEloRank).hasSize(1)
    assertThat(userByEloRank[0]).isEqualTo(PlayerElo("kermit", 2000))
  }*/

}
