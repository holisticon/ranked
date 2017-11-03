package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.cmd.CreateMatch
import de.holisticon.ranked.command.cmd.CreatePlayer
import de.holisticon.ranked.command.event.MatchCreated
import de.holisticon.ranked.command.event.PlayerCreated
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test
import java.time.LocalDateTime

class PlayerSpec() {
  val fixture: AggregateTestFixture<Player> = AggregateTestFixture(Player::class.java)

  @Test
  fun testShouldNotAllowAmendBelowCumQty() {
    fixture.`when`(CreatePlayer(UserName("1"), "kermit")).expectEvents(PlayerCreated(UserName("1"), "kermit"))
  }
}

class MatchSpec() {
  val fixture: AggregateTestFixture<Match> = AggregateTestFixture(Match::class.java)


  val now = LocalDateTime.now()
  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")

  val teamBlue = Team(piggy, kermit)
  val teamRed = Team(gonzo, fozzy)

  val set1 = MatchSet(goalsBlue = 6, goalsRed = 3, offenseBlue = piggy, offenseRed = gonzo)
  val set2 = MatchSet(goalsBlue = 2, goalsRed = 6, offenseBlue = kermit, offenseRed = fozzy)
  val set3 = MatchSet(goalsBlue = 6, goalsRed = 5, offenseBlue = piggy, offenseRed = fozzy)

  val sets = arrayOf(set1, set2, set3)

  @Test
  fun aggregate_create() {

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
        )
      )
  }
}
