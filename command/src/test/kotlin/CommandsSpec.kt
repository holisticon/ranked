package de.holisticon.ranked.command

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.validation.Validation

class CreateMatchSpec {
  val validator = Validation.buildDefaultValidatorFactory().validator


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
    val r = validator.validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, fozzy), matchSets = ArrayList()))
    assertThat(r).isEmpty()
  }

  @Test
  fun `a match is *not* valid when a player is in both teams`() {
    val r = validator.validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, piggy), matchSets = ArrayList())).toList()

    assertThat(r.size).isEqualTo(1)
    assertThat(r[0].message).isEqualTo("A player must only be a member of one team.")
  }

  @Test
  fun `a match is *not* valid when a player from either team plays offense`() {
    val r = validator.validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, scooter), matchSets = sets)).toList()

    assertThat(r.size).isEqualTo(1)
    assertThat(r[0].message).isEqualTo("A player must be in the team to play offense in a set.")
  }

  @Test
  fun `a match must have a non-empty id`() {
    val r = validator.validate(CreateMatch(matchId = "", teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = ArrayList() )).toList()
    assertThat(r.size).isEqualTo(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.NotEmpty.message}")
  }
}
