@file:Suppress("SpringKotlinAutowiring")

package de.holisticon.ranked.command

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.holisticon.ranked.command.api.CancelParticipation
import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.extension.defaultSmartLifecycle
import de.holisticon.ranked.extension.send
import de.holisticon.ranked.extension.trackingEventProcessors
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.internal.InitUser
import mu.KLogging
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.common.jpa.EntityManagerProvider
import org.axonframework.common.transaction.TransactionManager
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.tokenstore.inmemory.InMemoryTokenStore
import org.axonframework.eventsourcing.eventstore.jpa.JpaEventStorageEngine
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.axonframework.messaging.interceptors.LoggingInterceptor
import org.axonframework.serialization.Serializer
import org.axonframework.serialization.upcasting.event.EventUpcaster
import org.axonframework.serialization.upcasting.event.EventUpcasterChain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.util.function.Consumer
import javax.sql.DataSource
import javax.validation.ValidatorFactory

/**
 * This is the main spring configuration class for everything axon/command related.
 */

@Configuration
class CommandConfiguration {

  companion object : KLogging() {
    const val REPLAY_PHASE = Int.MAX_VALUE - 10
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
    config.registerHandlerInterceptor("messageMonitor", { LoggingInterceptor() })
  }

  /**
   * Provide bean validation validator.
   */
  @Bean
  fun validatorFactoryBean(): ValidatorFactory = LocalValidatorFactoryBean()


  @Bean
  @Suppress("ObjectLiteralToLambda")
  fun onInitUser(commandGateway: CommandGateway) = object : Consumer<InitUser> {

    @EventListener
    override fun accept(user: InitUser) {
      val userName = UserName(user.id)
      with(commandGateway) {
        send(
          command = CheckPlayer(userName),
          success = { _, _: Any -> send<Any>(CancelParticipation(userName)) },
          failure = { _, _: Throwable -> send<Any>(CreatePlayer(userName = userName, displayName = user.name, imageUrl = user.imageUrl)) }
        )
      }
    }
  }

  @Bean
  fun tokenStore() = InMemoryTokenStore()

  @Bean
  fun jpaEventStorageEngine(serializer: Serializer, dataSource: DataSource, upcasters: List<EventUpcaster>, entityManagerProvider: EntityManagerProvider, transactionManager: TransactionManager) =
    JpaEventStorageEngine(serializer,
      EventUpcasterChain(upcasters),
      dataSource,
      entityManagerProvider,
      transactionManager)

  @Bean
  fun jacksonDateTime() = JavaTimeModule()

  @Bean
  fun jacksonKotlin() = KotlinModule()

}

