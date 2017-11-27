package de.holisticon.ranked.command.saga

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Test
import java.time.LocalDateTime

class MatchSagaSpec {

  private val fixture: SagaTestFixture<EloMatchSaga> = SagaTestFixture(EloMatchSaga::class.java)
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
  fun `start match saga`() {

    fixture.givenNoPriorActivity()
      .whenPublishingA(MatchCreated(matchId="4711", teamBlue= teamBlue, teamRed = teamRed, date = now, matchSets = sets, tournamentId = null))
      .expectActiveSagas(1)
  }
}
