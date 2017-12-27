package de.holisticon.ranked.command.service

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Component

@Component
class UserService {
  // TODO replace with real User / Identity service
  fun findUser(username: String): User = User(username, username.toUpperCase())
}

data class User(
  val userName: String,
  val displayName: String
)

/**
 * Startup user creation.
 */
@Component
class UserInitializer(val commandGateway: CommandGateway) : DefaultSmartLifecycle(Int.MAX_VALUE - 20) {

  override fun onStart() {

    arrayOf("kermit", "piggy", "gonzo", "fozzy", "beeker").forEach {
      commandGateway.send(
        CheckPlayer(userName = UserName(it)),
        object : CommandCallback<CheckPlayer, Any> {
          override fun onSuccess(commandMessage: CommandMessage<out CheckPlayer>?, result: Any?) {
            // player exists - do nothing
          }

          override fun onFailure(commandMessage: CommandMessage<out CheckPlayer>?, cause: Throwable?) {
            commandGateway.send<Any>(CreatePlayer(userName = UserName(it)))
          }
        }
      )
    }
  }
}

