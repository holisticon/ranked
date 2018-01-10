package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.properties.createProperties
import de.holisticon.ranked.service.elo.EloCalculationService
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Before
import org.junit.Test

class EloSagaSpec {

  private val eloSaga: SagaTestFixture<EloMatchSaga> = SagaTestFixture(EloMatchSaga::class.java)

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
    eloSaga.registerResource(EloCalculationService(createProperties(eloDefault = 100)))
  }

  @Test
  fun `elo saga started on match creation and registers participation`() {

    eloSaga
      .givenNoPriorActivity()
      .whenAggregate("4711")
      .publishes(MatchCreated(
        matchId = "4711",
        teamBlue = teamBlue,
        teamRed = teamRed,
        matchSets = sets,
        tournamentId = null))
      .expectActiveSagas(1)
      .expectDispatchedCommands(
        ParticipateInMatch(piggy, "4711"),
        ParticipateInMatch(kermit, "4711"),
        ParticipateInMatch(gonzo, "4711"),
        ParticipateInMatch(fozzy, "4711")
      )
  }

  @Test
  fun `elo saga collects initial elo, calculates the new rating and send commands to the player to update the ranking`() {
    eloSaga
      .givenAggregate("4711")
      .published(
        MatchCreated(
          matchId = "4711",
          teamBlue = teamBlue,
          teamRed = teamRed,
          matchSets = sets,
          tournamentId = null))
      .andThenAggregate("piggy")
      .published(PlayerParticipatedInMatch(piggy, "4711", 1000))
      .andThenAggregate("kermit")
      .published(PlayerParticipatedInMatch(kermit, "4711", 1000))
      .andThenAggregate("gonzo")
      .published(PlayerParticipatedInMatch(gonzo, "4711", 1000))
      .andThenAggregate("fozzy")
      .published(PlayerParticipatedInMatch(fozzy, "4711", 1000))

      .whenPublishingA(TeamWonMatch("4711", teamBlue, teamRed))
      .expectDispatchedCommands(
        UpdatePlayerRanking(
          matchId = "4711",
          userName = piggy,
          eloRanking = 1005),
        UpdatePlayerRanking(
          matchId = "4711",
          userName = kermit,
          eloRanking = 1005),
        UpdatePlayerRanking(
          matchId = "4711",
          userName = gonzo,
          eloRanking = 995),
        UpdatePlayerRanking(
          matchId = "4711",
          userName = fozzy,
          eloRanking = 995))
      .expectActiveSagas(0)
  }
}
