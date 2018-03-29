package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.*
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.model.user.User
import de.holisticon.ranked.properties.createProperties
import de.holisticon.ranked.service.user.UserService
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class PlayerSpec {

  companion object {
    val elo = 1337
  }

  private val fixture: AggregateTestFixture<Player> = AggregateTestFixture(Player::class.java)
  private val userService = mock(UserService::class.java)

  @Before
  fun init() {
    fixture.registerInjectableResource(createProperties(eloDefault = elo))
    fixture.registerInjectableResource(userService)
  }

  @Ignore("skip")
  @Test
  fun `when a createPlayer command is received, a player is created`() {
    Mockito.`when`(userService.loadUser("kermit")).thenReturn(User(id = "kermit", name = "KERMIT", imageUrl = "/kermit.png"))

    fixture
      .givenNoPriorActivity()
      .`when`(CreatePlayer(UserName("kermit"), "KERMIT", "/kermit.jpg"))
      .expectEvents(PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", imageUrl = "/kermit.jpg", initialElo = elo))
  }

  @Test
  fun `when a player participates in a match, his elo ranking is published`() {
    fixture
      .given(PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", imageUrl = "/kermit.jpg", initialElo = elo))
      .`when`(ParticipateInMatch(UserName("kermit"), "4711"))
      .expectEvents(PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
  }

  @Test
  fun `when a player participates in a match, he can not participate in another`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT",imageUrl = "/kermit.jpg",initialElo = elo),
        PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
      .`when`(ParticipateInMatch(UserName("kermit"), "4712"))
      .expectNoEvents()
      .expectException(IllegalStateException::class.java)
  }

  @Test
  fun `when a player participates in no match, he can participate in another`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", imageUrl = "/kermit.jpg",initialElo = elo),
        PlayerParticipatedInMatch(UserName("kermit"), "4711", elo),
        ParticipationCancelled(UserName("kermit")))
      .`when`(ParticipateInMatch(UserName("kermit"), "4712"))
      .expectEvents(PlayerParticipatedInMatch(UserName("kermit"), "4712", elo))
  }


  @Test
  fun `when a player participation is cancelled the is announced`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT",imageUrl = "/kermit.jpg", initialElo = elo),
        PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
      .`when`(CancelParticipation(UserName("kermit")))
      .expectEvents(ParticipationCancelled(UserName("kermit")))
  }



  @Test
  fun `when a player wins in a match, his elo ranking is published`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", imageUrl = "/kermit.jpg",initialElo = elo),
        PlayerParticipatedInMatch(UserName("kermit"), "4711", elo))
      .`when`(UpdatePlayerRanking(UserName("kermit"), "4711", elo + 67))
      .expectEvents(PlayerRankingChanged(UserName("kermit"), elo + 67))
  }

  @Test
  fun `when a player exists, it can be checked`() {
    fixture
      .given(
        PlayerCreated(userName = UserName("kermit"), displayName = "KERMIT", imageUrl = "/kermit.jpg",initialElo = elo))
      .`when`(CheckPlayer(UserName("kermit")))
      .expectEvents(PlayerExists(UserName("kermit")))
  }

}
