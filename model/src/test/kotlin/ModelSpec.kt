package de.holisticon.ranked.model


import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.junit.Test
import javax.validation.Validation

val validator = Validation.buildDefaultValidatorFactory().validator
val piggy = UserName("piggy")
val kermit = UserName("kermit")
val gonzo = UserName("gonzo")


class UserNameSpec {
  @Test
  fun `userName is created with value`() {
    assertThat(validator.validate(kermit)).isEmpty()
    assertThat(kermit.value).isEqualTo("kermit")
  }

  @Test
  fun `userName is not valid if value is ""`() {
    assertThat(validator.validate(UserName("")).toList()[0].message)
      .isEqualTo("The UserName must be at least 4 chars long!")
  }

  @Test
  fun `userName is not valid if value is shorter than 4 chars`() {
    assertThat(validator.validate(UserName("abc")).toList()[0].message)
      .isEqualTo("The UserName must be at least 4 chars long!")
  }
}

class MatchSetSpec {

  @Test
  fun `goalsRed must be in 0-6`() {
    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = -1,
        offenseBlue = piggy,
        offenseRed = kermit)).toList()[0].message)
      .isEqualTo("Goals must be between 0 and 6!")

    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = 7,
        offenseBlue = piggy,
        offenseRed = kermit)).toList()[0].message)
      .isEqualTo("Goals must be between 0 and 6!")

    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = 6,
        offenseBlue = piggy,
        offenseRed = kermit)).toList())
      .isEmpty()
  }
}

class TeamSpec {

  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")

  @Test
  fun `a team can be represented as set of players`() {
    val team = Team(piggy, kermit)

    assertThat(setOf(team.player1, team.player2))
      .hasSize(2)
      .containsExactly(piggy, kermit)
  }

  @Test
  fun `team(piggy,kermit) hasMember kermit`() {
    assertThat(Team(piggy,kermit) hasMember kermit ).isTrue()
  }

  @Test
  fun `team(piggy,kermit) not hasMember gonzo`() {
    assertThat(Team(piggy,kermit).hasMember(gonzo)).isFalse()
  }

  @Test
  fun `two teams are identical if they have the same players`() {
    assertThat(Team(piggy, kermit)).isEqualTo(Team(piggy, kermit))
  }

  @Test
  fun `a team must have two different players`() {
    val result = validator.validate(Team(piggy, piggy)).toList()

    assertThat(result[0].message).isEqualTo("A team must have two different players!")
  }
}
