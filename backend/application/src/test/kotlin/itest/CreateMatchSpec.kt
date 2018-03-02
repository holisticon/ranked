package de.holisticon.ranked

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDateTime
import java.util.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
class CreateMatchSpec {

  @Autowired
  lateinit var rest: TestRestTemplate

  @Autowired
  lateinit var commandGateway: CommandGateway

  val kermit = UserName("kermit")
  val gonzo = UserName("gonzo")
  val piggy = UserName("piggy")
  val fozzy = UserName("fozzy")

  @Before
  fun `init players`() {
    commandGateway.send<Any>(CreatePlayer(userName = kermit))
    commandGateway.send<Any>(CreatePlayer(userName = gonzo))
    commandGateway.send<Any>(CreatePlayer(userName = piggy))
    commandGateway.send<Any>(CreatePlayer(userName = fozzy))
  }


  @DirtiesContext
  @Test
  fun `valid match results in successful response`() {

    val body = CreateMatch(
      matchId = UUID.randomUUID().toString(),
      startTime = LocalDateTime.now(),
      teamBlue = Team(kermit, piggy),
      teamRed = Team(fozzy, gonzo),
      matchSets = listOf(
        MatchSet(goalsRed = 6, goalsBlue = 1, offenseRed = fozzy, offenseBlue = kermit),
        MatchSet(goalsRed = 6, goalsBlue = 1, offenseRed = gonzo, offenseBlue = piggy)
      )
    )
    val httpResult = rest.postForEntity("/command/match", body, Void::class.java)
    assertThat(httpResult.statusCode).isEqualTo(HttpStatus.NO_CONTENT)

  }

  @Test
  fun `match with no winner results in bad request error`() {
    val body = CreateMatch(
      matchId = UUID.randomUUID().toString(),
      startTime = LocalDateTime.now(),
      teamBlue = Team(kermit, piggy),
      teamRed = Team(fozzy, gonzo),
      matchSets = listOf(
        MatchSet(goalsRed = 6, goalsBlue = 1, offenseRed = fozzy, offenseBlue = kermit),
        MatchSet(goalsRed = 1, goalsBlue = 6, offenseRed = gonzo, offenseBlue = piggy)
      )
    )
    val httpResult = rest.postForEntity("/command/match", body, Void::class.java)
    assertThat(httpResult.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
  }

}

