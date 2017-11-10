package de.holisticon.ranked.model


import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.dsl.it
import org.junit.Ignore
import org.junit.Test
import javax.validation.Validation
import javax.validation.Validator

val validator = Validation.buildDefaultValidatorFactory().validator
val piggy = UserName("piggy")
val kermit = UserName("kermit")
val gonzo = UserName("gonzo")

fun Validator.singleMessage( target: Any) = this.validate(target).toList()[0].message!!

class UserNameSpec {
  @Test
  fun `userName is created with value`() {
    assertThat(validator.validate(kermit)).isEmpty()
    assertThat(kermit.value).isEqualTo("kermit")
  }

  @Test
  fun `userName is not valid if value is ""`() {
    assertThat(validator.singleMessage(UserName("")))
      .isEqualTo("The UserName must be at least 4 chars long!")
  }

  @Test
  fun `userName is not valid if value is shorter than 4 chars`() {
    assertThat(validator.singleMessage(UserName("abc")))
      .isEqualTo("The UserName must be at least 4 chars long!")
  }
}

class MatchSetSpec {

  @Test
  fun `goalsRed must be in 0-6`() {
    assertThat(validator.singleMessage(
      MatchSet(
        goalsBlue = 4,
        goalsRed = -1,
        offenseBlue = piggy,
        offenseRed = kermit)))
      .isEqualTo("Goals must be between 0 and 6!")

    assertThat(validator.singleMessage(
      MatchSet(
        goalsBlue = 4,
        goalsRed = 7,
        offenseBlue = piggy,
        offenseRed = kermit)))
      .isEqualTo("Goals must be between 0 and 6!")

    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = 6,
        offenseBlue = piggy,
        offenseRed = kermit)).toList())
      .isEmpty()
  }

  @Test
  fun `winner is BLUE`() {
    val s = MatchSet(
      goalsBlue = 6,
      goalsRed = 3,
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.validate(s)).isEmpty()

    assertThat(s.winner()).isEqualTo(Team.BLUE)
  }
  @Test
  fun `winner is RED`() {
    val s = MatchSet(
      goalsBlue = 2,
      goalsRed = 6,
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.validate(s)).isEmpty()

    assertThat(s.winner()).isEqualTo(Team.RED)
  }

  @Test
  @Ignore
  fun `not valid - no team won`() {
    val s = MatchSet(
      goalsBlue = 2,
      goalsRed = 4,
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.singleMessage(s)).isEqualTo("One team must have 6 goals to count the set!")
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
  fun `two teams are identical if they have the same players no matter in which order`() {
    assertThat(Team(piggy, kermit)).isEqualTo(Team(piggy, kermit))
    assertThat(Team(piggy, kermit)).isEqualTo(Team(kermit, piggy))
  }

  @Test
  fun `a team must have two different players`() {
    val result = validator.validate(Team(piggy, piggy)).toList()

    assertThat(result[0].message).isEqualTo("A team must have two different players!")
  }
}
