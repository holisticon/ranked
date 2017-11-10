package de.holisticon.ranked.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import javax.validation.Validation

class TeamSpec {

  val validator = Validation.buildDefaultValidatorFactory().validator

  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")

  @Test
  fun `a team can be represented as set of players`() {
    val team = Team(piggy, kermit)

    assertThat(team.players)
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