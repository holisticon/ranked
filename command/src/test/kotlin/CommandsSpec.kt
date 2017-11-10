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

  val set1 = MatchSet(goalsBlue = 6, goalsRed = 3, offenseBlue = piggy, offenseRed = gonzo)
  val set2 = MatchSet(goalsBlue = 2, goalsRed = 6, offenseBlue = kermit, offenseRed = fozzy)
  val set3 = MatchSet(goalsBlue = 6, goalsRed = 5, offenseBlue = piggy, offenseRed = fozzy)

  val sets = listOf(set1, set2, set3)

  @Test
  fun `a match is valid when no player is in both teams`() {
    val c = CreateMatch(teamRed = Team(piggy,kermit), teamBlue = Team(gonzo,fozzy), matchSets = sets)

    val r = validator.validate(c)
    assertThat(r).isEmpty()
  }

  @Test
  fun `a match is *not* valid when a player is in both teams`  () {
    val c = CreateMatch(teamRed = Team(piggy, kermit), teamBlue = Team(gonzo, piggy), matchSets = sets)

    val r = validator.validate(c).toList()

    assertThat(r[0].message).isEqualTo("A player must only be in one of the teams!")

  }
}
