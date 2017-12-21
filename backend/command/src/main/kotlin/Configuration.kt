package de.holisticon.ranked.command

import de.holisticon.ranked.axon.TrackingProcessors
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.model.event.internal.ReplayTrackingProcessor
import de.holisticon.ranked.properties.RankedProperties
import mu.KLogging
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import java.util.*
import javax.validation.ValidatorFactory

/**
 * Configure components.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
class CommandConfiguration {

  @Autowired
  fun configure(bus: SimpleCommandBus, validationFactory: ValidatorFactory) {
    bus.registerDispatchInterceptor(BeanValidationInterceptor(validationFactory))
  }

  @Autowired
  fun registerTrackingProcessors(trackingProcessorService: TrackingProcessorService) {
    trackingProcessorService.registerTrackingProcessors()
  }


  @Bean
  fun validatorFactoryBean(): ValidatorFactory = LocalValidatorFactoryBean()
}

/**
 * Startup axon tracking processor replay.
 */
@Component
class TrackingProcessorInitializer(val trackingProcessorService: TrackingProcessorService) : DefaultSmartLifecycle() {

  override fun start() {
    this.trackingProcessorService.replayAll()
    super.start()
  }

  override fun getPhase(): Int {
    return Int.MAX_VALUE - 10
  }
}

/**
 * Token JPA repository
 */
interface TokenJpaRepository : JpaRepository<TokenEntry, TokenEntry.PK>

/**
 * Tracking processor service.
 */
@Component
class TrackingProcessorService(
  val eventHandlingConfiguration: EventHandlingConfiguration,
  val repository: TokenJpaRepository,
  val properties: RankedProperties
) {

  companion object : KLogging()

  @Autowired
  lateinit var trackingProcessors: TrackingProcessors

  fun registerTrackingProcessors() {
    trackingProcessors.forEach { name ->
      logger.info { "Registering tracking processor $name." }
      eventHandlingConfiguration.registerTrackingProcessor(name)
    }
  }

  @EventListener
  fun replay(event: ReplayTrackingProcessor) {
    logger.info { "Replay requested: $event" }
    val id: TokenEntry.PK = TokenEntry.PK(event, 0)
    val one: Optional<TokenEntry> = repository.findById(id)
    if (one.isPresent) {
      val processor: Optional<EventProcessor> = this.eventHandlingConfiguration.getProcessor(event)
      processor.ifPresent({ p ->
        logger.debug { "Stopping $event" }
        p.shutDown()
        logger.debug { "Deleting token for $event" }
        this.repository.deleteById(id)
        logger.debug { "Starting $event" }
        p.start()
      })
    } else {
      logger.info { "Token not found for $event processor. No replay initiated." }
    }
  }

  fun replayAll() = trackingProcessors.forEach { replay(it) }

}
