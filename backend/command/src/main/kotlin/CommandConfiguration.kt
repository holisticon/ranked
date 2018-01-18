@file:Suppress("SpringKotlinAutowiring")

package de.holisticon.ranked.command

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.data.TokenJpaRepository
import de.holisticon.ranked.command.saga.EloMatchSaga
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.service.user.UserService
import mu.KLogging
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.config.SagaConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.TrackingEventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.axonframework.spring.config.AxonConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.util.*
import javax.validation.ValidatorFactory

/**
 * This is the main spring configuration class for everything axon/command related.
 */

@Configuration
class CommandConfiguration() {

  companion object : KLogging() {
    const val REPLAY_PHASE = Int.MAX_VALUE - 10
    const val REPLAY_SEGMENT = 0
  }

  /**
   * Enable bean validation for incoming commands.
   */
  @Autowired
  fun configure(bus: SimpleCommandBus, validationFactory: ValidatorFactory) {
    bus.registerDispatchInterceptor(BeanValidationInterceptor(validationFactory))
  }

  @Autowired
  fun configure(config: EventHandlingConfiguration) {
    config.registerHandlerInterceptor( "messageMonitor", { LoggingInterceptor() } )
  }

  /**
   * Provide bean validation validator.
   */
  @Bean
  fun validatorFactoryBean(): ValidatorFactory = LocalValidatorFactoryBean()

  /**
   * Register Lifecycle handler for event replay.
   */
  @Bean
  fun replayTrackingProcessors(
    configuration: EventHandlingConfiguration,
    repository: TokenJpaRepository
  ) = object : DefaultSmartLifecycle(REPLAY_PHASE) {
    override fun onStart() {
      val trackingProcessorTokenList = trackingProcessorTokenList(configuration, repository)

      // first, all processors are stopped
      trackingProcessorTokenList.forEach {
        logger.debug { "Stopping ${it.name}" }
        it.shutDown()
      }

      // then all token are deleted
      trackingProcessorTokenList.forEach {
        logger.debug { "Deleting token for ${it.name}" }
        it.deleteToken()
      }

      trackingProcessorTokenList.forEach {
        logger.debug { "Starting ${it.name}" }
        it.start()
      }
    }
  }

  /**
   * Provides List of processor/token pairs based on [trackingProcessorNames].
   */
  fun trackingProcessorTokenList(configuration: EventHandlingConfiguration, repository: TokenJpaRepository): List<TrackingProcessorToken> {
    val pairs = mutableListOf<TrackingProcessorToken>()

    configuration.processors
      .filter { it is TrackingEventProcessor }
      .forEach {

        val id: TokenEntry.PK = TokenEntry.PK(it.name, CommandConfiguration.REPLAY_SEGMENT)
        val token: Optional<TokenEntry> = repository.findById(id)

        if (token.isPresent) {
          pairs.add(TrackingProcessorToken(processor = it, token = token.get(), repository = repository))
        }
      }

    logger.info { "collected processors for replay: ${pairs.map { it.name }}" }

    return pairs.toList()
  }

  @Bean
  fun initializeUsers(commandGateway: CommandGateway, userService: UserService) = object : DefaultSmartLifecycle(Int.MAX_VALUE - 20) {

    override fun onStart() {

      userService.loadAll().forEach {
        commandGateway.send(
          CheckPlayer(userName = UserName(it.id)),
          object : CommandCallback<CheckPlayer, Any> {
            override fun onSuccess(commandMessage: CommandMessage<out CheckPlayer>?, result: Any?) {
              // player exists - do nothing
            }

            override fun onFailure(commandMessage: CommandMessage<out CheckPlayer>?, cause: Throwable?) {
              commandGateway.send<Any>(CreatePlayer(userName = UserName(it.id)))
            }
          }
        )
      }
    }
  }


}

@Configuration
class SagaConfig {

  @Lazy
  @Bean("eloSagaConfig")
  fun eloSagaConfig() : SagaConfiguration<EloMatchSaga> {
    return SagaConfiguration.subscribingSagaManager(EloMatchSaga::class.java)
  }


}

class TrackingProcessorToken(
  private val processor: EventProcessor,
  private val token: TokenEntry,
  private val repository: TokenJpaRepository
) {

  val name by lazy {
    processor.name
  }

  val id by lazy {
    TokenEntry.PK(token.processorName, token.segment)
  }

  fun shutDown() = processor.shutDown()

  fun start() = processor.start()

  fun deleteToken() {
    repository.deleteById(id)
  }

}
