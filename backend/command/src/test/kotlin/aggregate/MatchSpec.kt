package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerPosition
import de.holisticon.ranked.model.event.PlayerWonMatchSet
import de.holisticon.ranked.model.event.TeamWonMatchSet
import de.holisticon.ranked.properties.RankedProperties
import de.holisticon.ranked.properties.createProperties
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.time.LocalDateTime

@RunWith(SpringRunner::class)
// TODO replace with real props
/*
@TestPropertySource(
  properties = arrayOf(
    "ranked.score.match=2",
    "ranked.score.set=6",
    "ranked.elo.default=1000"
  )
)*/
@ContextConfiguration(classes = arrayOf(MatchSpec.MatchSpecTestConfiguration::class))
class MatchSpec {

  private val fixture: AggregateTestFixture<Match> = AggregateTestFixture(Match::class.java)

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

  @Autowired
  private lateinit var matchService: MatchService

  @Autowired
  private lateinit var properties: RankedProperties

  @Before
  fun init() {
    fixture.registerInjectableResource(matchService)
    fixture.registerInjectableResource(properties)
  }


  @Test
  fun `match create command results in a match created event`() {

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
        ),
        TeamWonMatchSet(team = teamBlue, looser = teamRed, offense = piggy, date = now, matchId = "4711"),
        PlayerWonMatchSet(piggy, PlayerPosition.OFFENSE, kermit, now),
        PlayerWonMatchSet(kermit, PlayerPosition.DEFENSE, piggy, now),

        TeamWonMatchSet(team = teamRed, looser = teamBlue, offense = fozzy, date = now, matchId = "4711"),
        PlayerWonMatchSet(gonzo, PlayerPosition.DEFENSE, fozzy, now),
        PlayerWonMatchSet(fozzy, PlayerPosition.OFFENSE, gonzo, now),

        TeamWonMatchSet(team = teamBlue, looser = teamRed, offense= piggy, date = now, matchId = "4711"),
        PlayerWonMatchSet(piggy, PlayerPosition.OFFENSE, kermit, now),
        PlayerWonMatchSet(kermit, PlayerPosition.DEFENSE, piggy, now)
      )
  }


  @TestConfiguration
  class MatchSpecTestConfiguration {

    @Bean
    fun validatorFactoryBean(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()

    @Bean
    fun matchService(props: RankedProperties): MatchService = MatchService(props)

    @Bean
    fun props(): RankedProperties = createProperties()
  }

}
