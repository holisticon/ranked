package de.holisticon.ranked.model


import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime
import javax.validation.Validation
import javax.validation.Validator

val validator = Validation.buildDefaultValidatorFactory().validator
val piggy = UserName("piggy")
val kermit = UserName("kermit")
val gonzo = UserName("gonzo")

fun Validator.singleMessage(target: Any) = this.validate(target).toList()[0].message!!

/**
 * User
 */
class UserNameSpec {
  @Test
  fun `userName is created with value`() {
    assertThat(validator.validate(kermit)).isEmpty()
    assertThat(kermit.value).isEqualTo("kermit")
  }

  @Test
  fun `userName is not valid if value is ""`() {
    assertThat(validator.singleMessage(UserName("")))
      .isEqualTo("The user-id must be at least 4 chars long.")
  }

  @Test
  fun `userName is not valid if value is shorter than 4 chars`() {
    assertThat(validator.singleMessage(UserName("abc")))
      .isEqualTo("The user-id must be at least 4 chars long.")
  }
}

/**
 * Team
 */
class TeamSpec {

  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")

  @Test
  fun `team(piggy,kermit) hasMember kermit`() {
    assertThat(Team(piggy, kermit) hasMember kermit).isTrue()
  }

  @Test
  fun `team(piggy,kermit) not hasMember gonzo`() {
    assertThat(Team(piggy, kermit).hasMember(gonzo)).isFalse()
  }

  @Test
  fun `two teams are identical if they have the same players no matter in which order`() {
    assertThat(Team(piggy, kermit)).isEqualTo(Team(piggy, kermit))
    assertThat(Team(piggy, kermit)).isEqualTo(Team(kermit, piggy))
  }

  @Test
  fun `a team must have two different players`() {
    val result = validator.validate(Team(piggy, piggy)).toList()

    assertThat(result[0].message).isEqualTo("A team must have two different players.")
  }
}


/**
 * Match set
 */
class MatchSetSpec {

  @Test
  fun `timed invalid user names`() {
    assertThat(validator.validate(
      TimedMatchSet(
        goals = (0..10).map {
          Pair(if (it % 2 == 0) {
            TeamColor.BLUE
          } else {
            TeamColor.RED
          }, LocalDateTime.now())
        },
        offenseBlue = UserName("a"),
        offenseRed = UserName("b"))).stream().map { v -> v.message })
      .containsExactlyInAnyOrder(
        "The user-id must be at least 4 chars long.",
        "The user-id must be at least 4 chars long."
      )
  }


  @Test
  fun `invalid user names`() {
    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 6,
        goalsRed = 0,
        offenseBlue = UserName("a"),
        offenseRed = UserName("b"))).stream().map { v -> v.message })
      .containsExactlyInAnyOrder(
        "The user-id must be at least 4 chars long.",
        "The user-id must be at least 4 chars long."
      )
  }


  @Test
  fun `goals timed must be in 0-6`() {
    assertThat(validator.validate(
      TimedMatchSet(
        goals = listOf(),
        offenseBlue = piggy,
        offenseRed = kermit)).stream().map { v -> v.message })
      .containsExactlyInAnyOrder(
        "One team must have 6 goals to count the set.",
        "A timed set must not be empty."
      )
  }

  @Test
  fun `timed winner is BLUE`() {

    val s = TimedMatchSet(
      goals = (0..10).map {
        Pair(if (it % 2 == 0) {
          TeamColor.BLUE
        } else {
          TeamColor.RED
        }, LocalDateTime.now())
      },
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.validate(s)).isEmpty()
    assertThat(s.winner()).isEqualTo(TeamColor.BLUE)
  }

  @Test
  fun `timed winner is RED`() {
    val s = TimedMatchSet(
      goals = (0..10).map {
        Pair(if (it % 2 == 1) {
          TeamColor.BLUE
        } else {
          TeamColor.RED
        }, LocalDateTime.now())
      },
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.validate(s)).isEmpty()
    assertThat(s.winner()).isEqualTo(TeamColor.RED)
  }

  @Test
  fun `timed not valid - no team won`() {

    assertThat(validator.singleMessage(
      TimedMatchSet(
        goals = (0..11).map {
          Pair(if (it % 2 == 1) {
            TeamColor.BLUE
          } else {
            TeamColor.RED
          }, LocalDateTime.now())
        },
        offenseBlue = piggy,
        offenseRed = kermit)
    )).isEqualTo("One team must have 6 goals to count the set.")

    assertThat(validator.singleMessage(
      TimedMatchSet(
        goals = (0..11).map {
          Pair(if (it % 2 == 0) {
            TeamColor.BLUE
          } else {
            TeamColor.RED
          }, LocalDateTime.now())
        },
        offenseBlue = piggy,
        offenseRed = kermit)
    )).isEqualTo("One team must have 6 goals to count the set.")

  }

  @Ignore
  @Test
  fun `goalsRed must be in 0-6`() {
    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = -1,
        offenseBlue = piggy,
        offenseRed = kermit)).stream().map { v -> v.message })
      .containsExactlyInAnyOrder(
        "Goals must be between 0 and 6."
      )

    assertThat(validator.validate(
      MatchSet(
        goalsBlue = 4,
        goalsRed = 7,
        offenseBlue = piggy,
        offenseRed = kermit)).stream().map { v -> v.message })
      .containsExactlyInAnyOrder(
        "Goals must be between 0 and 6."
      )

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
    assertThat(s.winner()).isEqualTo(TeamColor.BLUE)
  }

  @Test
  fun `winner is RED`() {
    val s = MatchSet(
      goalsBlue = 0,
      goalsRed = 6,
      offenseBlue = piggy,
      offenseRed = kermit)

    assertThat(validator.validate(s)).isEmpty()
    assertThat(s.winner()).isEqualTo(TeamColor.RED)
  }

  @Test
  fun `not valid - no team won`() {
    assertThat(validator.singleMessage(
      MatchSet(
        goalsBlue = 6,
        goalsRed = 6,
        offenseBlue = piggy,
        offenseRed = kermit)
    )).isEqualTo("One team must have 6 goals to count the set.")
  }

}

