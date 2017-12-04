package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.RankedProperties
import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatchSet
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class MatchSagaSpec {

  private val matchSaga: SagaTestFixture<MatchWinnerSaga> = SagaTestFixture(MatchWinnerSaga::class.java)
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

  @Before
  fun init() {
    matchSaga.registerResource(MatchService(RankedProperties(scoreToWinMatch = 2, scoreToWinSet = 6, defaultElo = 100)))
  }

  @Test
  fun `match saga is started on match creation`() {

    matchSaga
      .givenNoPriorActivity()
      .whenAggregate("4711")
      .publishes(MatchCreated(
        matchId = "4711",
        teamBlue = teamBlue,
        teamRed = teamRed,
        date = now,
        matchSets = sets,
        tournamentId = null))
      .expectActiveSagas(1)

  }

  @Test
  fun `match sage determines winner`() {
    matchSaga
      .givenAggregate("4711")
      .published(
        MatchCreated(
          matchId = "4711",
          teamBlue = teamBlue,
          teamRed = teamRed,
          date = now,
          matchSets = sets,
          tournamentId = null),
        TeamWonMatchSet(
          matchId = "4711",
          team = teamBlue,
          looser = teamRed,
          date = now,
          offense = set1.offenseBlue),
        TeamWonMatchSet(
          matchId = "4711",
          team = teamRed,
          looser = teamBlue,
          date = now,
          offense = set2.offenseRed))
      .whenPublishingA(
        TeamWonMatchSet(
          matchId = "4711",
          team = teamBlue,
          looser = teamRed,
          date = now,
          offense = set3.offenseBlue))
      .expectDispatchedCommands(
        WinMatch(
          matchId = "4711",
          winner = teamBlue,
          looser = teamRed))
      .expectActiveSagas(0)
  }
}
