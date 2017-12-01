package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.service.UserService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test
import java.time.LocalDateTime

class PlayerSpec {
  private val fixture: AggregateTestFixture<Player> = AggregateTestFixture(Player::class.java)

  @Test
  fun `when a createPlayer command is received, a PlayerCreated event is processed`() {

    fixture.registerInjectableResource(UserService(1000))

    fixture.`when`(CreatePlayer(UserName("kermit")))
      .expectEvents(PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", initialElo = 1000))
  }
}
