package de.holisticon.ranked.command.team

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.rest.CommandApi
import de.holisticon.ranked.extension.send
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.PlayerExists
import de.holisticon.ranked.model.event.TeamCreated
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
class PlayerCreatingSaga() {

  @Autowired
  @Transient
  lateinit var commandGateway: CommandGateway

  private val missingPlayer: MutableSet<UserName> = mutableSetOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "id")
  fun on(e: TeamCreated) {

    arrayOf(e.team.player1, e.team.player2).forEach {

      // remember it
      missingPlayer.add(it)

      associateWith("userName", it.value)

      commandGateway.send(
        command = CheckPlayer(it),
        success = { _, _: Any -> CommandApi.logger.debug { "Player $it exists." } },
        failure = { _, _: Throwable ->
          // player don't exist
          // create it
          commandGateway.send(
            CreatePlayer(userName = it, displayName = it.value, imageUrl = ""),
            success = { _, _: Any -> CommandApi.logger.debug { "Player $it will be created." } },
            failure = { _, cause: Throwable -> throw cause }
          )
        }
      )
    }
  }

  @SagaEventHandler(associationProperty = "userName")
  fun on(e: PlayerExists) {
    missingPlayer.remove(e.userName)
    if (missingPlayer.isEmpty()) {
      end()
    }
  }

  @SagaEventHandler(associationProperty = "userName")
  fun on(e: PlayerCreated) {
    missingPlayer.remove(e.userName)
    if (missingPlayer.isEmpty()) {
      end()
    }
  }
}


