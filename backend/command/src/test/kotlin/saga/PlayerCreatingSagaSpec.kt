package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.team.PlayerCreatingSaga
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.TeamCreated
import org.axonframework.test.saga.SagaTestFixture
import org.junit.Test

class PlayerCreatingSagaSpec {

  private val playerSaga: SagaTestFixture<PlayerCreatingSaga> = SagaTestFixture(PlayerCreatingSaga::class.java)

  val username1 = UserName("Team Alpha_player1")
  val username2 = UserName("Team Alpha_player2")

  @Test
  fun `player saga started on team creation and checks for players`() {

    playerSaga
      .givenNoPriorActivity()
      .whenAggregate("team-alpha")
      .publishes(TeamCreated(
        id = "team-alpha",
        name = "Team Alpha",
        team = Team(username1, username2)
      ))
      .expectActiveSagas(1)
      .expectDispatchedCommands(
        CheckPlayer(username1),
        CheckPlayer(username2)
      )
  }

}
