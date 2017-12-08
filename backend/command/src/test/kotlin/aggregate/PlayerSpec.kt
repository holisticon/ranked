package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.command.service.UserService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.PlayerRankingChanged
import de.holisticon.ranked.properties.createProperties
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test

class PlayerSpec {

  companion object {
    val elo = 1337
  }

  private val fixture: AggregateTestFixture<Player> = AggregateTestFixture(Player::class.java)

  @Before
  fun init() {
    fixture.registerInjectableResource(createProperties(eloDefault = elo))
    fixture.registerInjectableResource(UserService())
  }


  @Test
  fun `when a createPlayer command is received, a player is created`() {
    fixture
      .givenNoPriorActivity()
      .`when`(CreatePlayer(UserName("kermit")))
      .expectEvents(PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", initialElo = elo))
  }

  @Test
  fun `when a player participates in a match, his elo ranking is published`() {
    fixture
      .given(PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", initialElo = elo))
      .`when`(ParticipateInMatch(UserName("kermit"), "4711"))
      .expectEvents(PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
  }

  @Test
  fun `when a player wins in a match, his elo ranking is published`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", initialElo = elo),
        PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
      .`when`(UpdatePlayerRanking(UserName("kermit"), "4711", elo + 67))
      .expectEvents(PlayerRankingChanged(UserName("kermit"), elo + 67))
  }

}
