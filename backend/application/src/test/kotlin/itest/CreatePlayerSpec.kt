package de.holisticon.ranked

import de.holisticon.ranked.command.rest.CommandApi
import de.holisticon.ranked.model.Player
import de.holisticon.ranked.model.UserName
import org.assertj.core.api.Assertions.assertThat
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.TimeUnit

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("itest")
class CreatePlayerSpec {

  @Autowired
  lateinit var rest: TestRestTemplate

  @Test
  fun `a player can be created and found via rest`() {
    val expected = Player(userName = UserName("GoenssotheGraet"), displayName = "Gönßo the Grät", imageUrl = "/gonzo.jpg", eloRanking = 1000)

    val response = rest.postForEntity(
      "/command/player",
      CommandApi.PlayerInfo(expected.displayName, expected.imageUrl),
      Void::class.java)
    assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)

    val gonzo = rest.getForObject("/view/player/${expected.userName.value}", Player::class.java)

    assertThat(gonzo).isEqualTo(expected)
  }
}
