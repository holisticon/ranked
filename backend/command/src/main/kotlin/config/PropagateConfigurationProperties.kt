package de.holisticon.ranked.command.config

import de.holisticon.ranked.command.api.CheckConfiguration
import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreateConfiguration
import de.holisticon.ranked.command.api.UpdateConfiguration
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.properties.RankedProperties
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Component


@Component
class PropagateConfigurationProperties(
  val commandGateway: CommandGateway,
  val properties: RankedProperties
) : DefaultSmartLifecycle() {

  override fun start() {
    commandGateway.send(CheckConfiguration(), object : CommandCallback<CheckConfiguration, Any>{
      override fun onFailure(commandMessage: CommandMessage<out CheckConfiguration>?, cause: Throwable?) {
        commandGateway.send<CreateConfiguration>(CreateConfiguration(properties = properties))
      }

      override fun onSuccess(commandMessage: CommandMessage<out CheckConfiguration>?, result: Any?) {
        commandGateway.send<UpdateConfiguration>(UpdateConfiguration(properties = properties))
      }
    })
  }

  override fun getPhase(): Int = Int.MAX_VALUE - 30
}
