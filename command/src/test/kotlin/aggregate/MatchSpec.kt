package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test
import java.time.LocalDateTime

class MatchSpec {

  private val fixture: AggregateTestFixture<Match> = AggregateTestFixture(Match::class.java)

  private val now = LocalDateTime.now()
  private val piggy = UserName("piggy")
  private val kermit = UserName("kermit")
  private val gonzo = UserName("gonzo")
  private val fozzy = UserName("fozzy")

  private val teamBlue = Team(piggy, kermit)
  private val teamRed = Team(gonzo, fozzy)

  private val set1 = MatchSet(goalsBlue = 6, goalsRed = 3, offenseBlue = piggy, offenseRed = gonzo)
  private val set2 = MatchSet(goalsBlue = 2, goalsRed = 6, offenseBlue = kermit, offenseRed = fozzy)
  private val set3 = MatchSet(goalsBlue = 6, goalsRed = 5, offenseBlue = piggy, offenseRed = fozzy)

  private val sets = listOf(set1, set2, set3)

  @Test
  fun `match create command results in a match created event`() {

    fixture
      .givenNoPriorActivity()
      .`when`(
        CreateMatch(
          matchId = "4711",
          date = now,
          teamRed = teamRed,
          teamBlue = teamBlue,
          matchSets = sets,
          tournamentId = "0815"
        )
      )
      .expectEvents(
        MatchCreated(
          matchId = "4711",
          date = now,
          teamRed = teamRed,
          teamBlue = teamBlue,
          matchSets = sets,
          tournamentId = "0815"
        ),
        TeamWonMatchSet(teamBlue, teamRed, piggy, now, "4711"),
        PlayerWonMatchSet(piggy, PlayerPosition.OFFENSE, kermit, now),
        PlayerWonMatchSet(kermit, PlayerPosition.DEFENSE, piggy, now),

        TeamWonMatchSet(teamRed, teamBlue, fozzy, now, "4711"),
        PlayerWonMatchSet(gonzo, PlayerPosition.DEFENSE, fozzy, now),
        PlayerWonMatchSet(fozzy, PlayerPosition.OFFENSE, gonzo, now),

        TeamWonMatchSet(teamBlue, teamRed, piggy, now, "4711"),
        PlayerWonMatchSet(piggy, PlayerPosition.OFFENSE, kermit, now),
        PlayerWonMatchSet(kermit, PlayerPosition.DEFENSE, piggy, now)

        /*
        TeamWonMatch("4711", teamBlue, teamRed, now),
        PlayerWonMatch(piggy, kermit, now),
        PlayerWonMatch(kermit, piggy, now)
        */
      )
  }
}
