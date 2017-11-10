package de.holisticon.ranked.command

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.event.internal.ReplayTrackingProcessor
import mu.KLogging
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

/**
 * The spring boot main application.
 */

// TODO: why is this a configuration?
@Configuration
class CommandConfiguration {

  /**
   * Configure Bean Validation for commands.
   */
  @Autowired
  fun configure(bus: SimpleCommandBus) {
    bus.registerDispatchInterceptor(BeanValidationInterceptor())
  }

  /**
   * Configure tracking processors
   */
  @Autowired
  fun configure(trackingProcessorService: TrackingProcessorService) {
    trackingProcessorService.registerTrackingProcessors()
    trackingProcessorService.replayAll()
  }
}

/**
 * Token JPA repository
 */
interface TokenJpaRepository : JpaRepository<TokenEntry, TokenEntry.PK>


@Component
class TrackingProcessorService(val eventHandlingConfiguration: EventHandlingConfiguration, val repository: TokenJpaRepository) {

  companion object : KLogging()

  fun registerTrackingProcessors() {
    trackingProcessors.forEach { name ->
      logger.info { "Registering tracking processor $name." }
      eventHandlingConfiguration.registerTrackingProcessor(name)
    }
  }

  @EventListener
  fun replay(event: ReplayTrackingProcessor) {
    logger.info { "replay requested: $event" }
    val name = event.name
    val id: TokenEntry.PK = TokenEntry.PK(name, 0)
    val one: TokenEntry = repository.getOne(id)
    if (one == null) {
      logger.warn { "Token not found for $name processor." }
    } else {
      val processor: Optional<EventProcessor> = this.eventHandlingConfiguration.getProcessor(name)
      processor.ifPresent({ p ->
        logger.debug { "Stopping $name" }
        p.shutDown()
        logger.debug { "Deleting token for $name" }
        this.repository.deleteById(id)
        logger.debug { "Starting $name" }
        p.start()
      })
    }
  }

  fun replayAll() = trackingProcessors.forEach { name -> replay(ReplayTrackingProcessor(name)) }

  // TODO replace with bean post-processing
  internal val trackingProcessors by lazy {
    val scanner = ClassPathScanningCandidateComponentProvider(false)
    scanner.addIncludeFilter(AnnotationTypeFilter(TrackingProcessor::class.java))
    scanner.findCandidateComponents("de.holisticon.ranked").stream()
      .map { bd ->
        Optional.ofNullable(classForBeanDefinition(bd).getAnnotation(ProcessingGroup::class.java))
          .map(ProcessingGroup::value)
          .orElse(classForBeanDefinition(bd).`package`.name)
      }
      .collect(Collectors.toList())
  }

  internal fun classForBeanDefinition(bd: BeanDefinition): Class<*> {
    return Class.forName(bd.beanClassName)
  }


}
