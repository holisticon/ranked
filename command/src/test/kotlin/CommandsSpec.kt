package de.holisticon.ranked.command

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.Validator

@RunWith(SpringRunner::class)
@TestPropertySource(
  properties = arrayOf(
    "ranked.score.match=2",
    "ranked.score.set=6"
  )
)
@ContextConfiguration(classes = arrayOf(CreateMatchSpec.MatchSpecTestConfiguration::class))
class CreateMatchSpec {

  val piggy = UserName("piggy")
  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val fozzy = UserName("fozzy")
  val scooter = UserName("scooter")

  val set1 = MatchSet(goalsBlue = 6, goalsRed = 3, offenseBlue = piggy, offenseRed = gonzo)
  val set2 = MatchSet(goalsBlue = 2, goalsRed = 6, offenseBlue = kermit, offenseRed = fozzy)
  val set3 = MatchSet(goalsBlue = 6, goalsRed = 5, offenseBlue = piggy, offenseRed = fozzy)
  val set4 = MatchSet(goalsBlue = 6, goalsRed = 1, offenseBlue = piggy, offenseRed = fozzy)

  val sets = listOf(set1, set2, set3)

  @Autowired
  private lateinit var validator: Validator

  private fun validate(obj: Any) = validator.validate(obj).toList()

  @Test
  fun `a match is valid when no player is in both teams`() {

    val r = validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, fozzy), matchSets = sets))
    assertThat(r).isEmpty()
  }

  @Test
  fun `a match is *not* valid when a player is in both teams`() {
    val r = validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, piggy), matchSets = listOf(set1, set1.copy(offenseBlue = kermit, offenseRed = piggy))))

    assertThat(r).hasSize(1)
    assertThat(r[0].message).isEqualTo("A player must only be a member of one team.")
  }

  @Test
  fun `a match is *not* valid when a player from either team plays offense`() {
    val r = validate(CreateMatch(teamBlue = Team(piggy, kermit), teamRed = Team(gonzo, scooter), matchSets = sets))

    assertThat(r).hasSize(1)
    assertThat(r[0].message).isEqualTo("A player must be in the team to play offense in a set.")
  }

  @Test
  fun `a match must have a non-empty id`() {
    val r = validate(CreateMatch(matchId = "", teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = sets))
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{javax.validation.constraints.NotEmpty.message}")
  }

  @Test
  fun `a match must have sets`() {
    val r = validate(CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = ArrayList()))
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{ranked.createMatch.finished}")
  }

  @Test
  fun `a match must have minimal 2 sets`() {
    val r = validate(CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = listOf(set1)))
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{ranked.createMatch.finished}")
  }

  @Test
  fun `a match must have maximum 3 sets`() {
    val match = CreateMatch(teamRed = Team(fozzy, gonzo), teamBlue = Team(kermit, piggy), matchSets = listOf(set1, set2, set3, set4))
    val r = validate(match)
    assertThat(r).hasSize(1)
    assertThat(r[0].messageTemplate).isEqualTo("{ranked.createMatch.finished}")
  }

  @TestConfiguration
  class MatchSpecTestConfiguration {

    @Bean
    fun validatorFactoryBean(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()

    @Bean
    fun matchService(): MatchService = MatchService(scoreToWinSet = 6, scoreToWinMatch = 2)
  }

}
