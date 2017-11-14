package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Test
import java.time.LocalDateTime

class PlayerSpec() {
  private val fixture: AggregateTestFixture<Player> = AggregateTestFixture(Player::class.java)
  private val now = LocalDateTime.now()

  @Test
  fun `when a createPlayer command is received, a PlayerCreated event is processed`() {

    fixture.`when`(CreatePlayer(UserName("kermit"), "Kermit the Frog", now))
      .expectEvents(PlayerCreated(UserName("kermit"), "Kermit the Frog", now))
  }
}
