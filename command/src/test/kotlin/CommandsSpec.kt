package de.holisticon.ranked.command

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.extension.validate
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CreateMatchSpec {

  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")
  val scooter = UserName("scooter")

  val set1 = MatchSet(goalsBlue = 6, goalsRed = 3, offenseBlue = piggy, offenseRed = gonzo)
  val set2 = MatchSet(goalsBlue = 2, goalsRed = 6, offenseBlue = kermit, offenseRed = fozzy)
  val set3 = MatchSet(goalsBlue = 6, goalsRed = 5, offenseBlue = piggy, offenseRed = fozzy)

  val sets = listOf(set1, set2, set3)

  @Test
  fun `a match is valid when no player is in both teams`() {
    val r = CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, fozzy), matchSets = sets).validate()
    assertThat(r).isEmpty()
  }

  @Test
  fun `a match is *not* valid when a player is in both teams`() {
    val r = CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, piggy), matchSets = listOf(set1, set1.copy(offenseBlue = kermit, offenseRed = piggy))).validate()

    assertThat(r).hasSize(1)
    assertThat(r[0].message).isEqualTo("A player must only be a member of one team.")
  }

  @Test
  fun `a match is *not* valid when a player from either team plays offense`() {
    val r = CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, scooter), matchSets = sets).validate()

    assertThat(r).hasSize(1)
    assertThat(r[0].message).isEqualTo("A player must be in the team to play offense in a set.")
  }

  @Test
  fun `a match must have a non-empty id`() {
    val r = CreateMatch(matchId = "", teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = sets ).validate()
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.NotEmpty.message}")
  }

  @Test
  fun `a match must have sets`() {
    val r = CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = ArrayList() ).validate()
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.Size.message}")
  }

  @Test
  fun `a match must minimal 2 sets`() {
    val r = CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = listOf(set1) ).validate()
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.Size.message}")
  }

  @Test
  fun `a match must maximum 3 sets`() {
    val r = CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = listOf(set1, set2, set1, set2) ).validate()
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.Size.message}")
  }
}
