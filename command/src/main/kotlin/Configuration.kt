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
import org.springframework.context.SmartLifecycle
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Collectors

/**
 * Configure Axon components.
 */
@Configuration
class CommandConfiguration {

  /**
   * Configure Bean Validation for commands.
   */
  @Autowired
  fun configure(bus: SimpleCommandBus) {
    bus.registerDispatchInterceptor(BeanValidationInterceptor())
  }


  @Autowired
  fun registerTrackingProcessors(trackingProcessorService: TrackingProcessorService) {
    trackingProcessorService.registerTrackingProcessors()
  }
}

/**
 * Startup axon tracking processor registration and replay.
 */
@Component
class TrackingProcessorInitializer(val trackingProcessorService: TrackingProcessorService) : SmartLifecycle {

  var running: Boolean = false

  override fun start() {

    this.trackingProcessorService.replayAll()
    this.running = true
  }

  override fun isAutoStartup(): Boolean { return true }

  override fun stop(callback: Runnable?) {
    if (callback != null) {
      callback.run()
    }
    this.running = false
  }

  override fun stop() { this.running = false }

  override fun getPhase(): Int {
    return Int.MAX_VALUE - 10
  }

  override fun isRunning(): Boolean { return running }
}

/**
 * Token JPA repository
 */
interface TokenJpaRepository : JpaRepository<TokenEntry, TokenEntry.PK>

/**
 * Tracking processor service.
 */
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
    logger.info { "Replay requested: $event" }
    val name = event.name
    val id: TokenEntry.PK = TokenEntry.PK(name, 0)
    val one: Optional<TokenEntry> = repository.findById(id)
    if (one.isPresent) {
      val processor: Optional<EventProcessor> = this.eventHandlingConfiguration.getProcessor(name)
      processor.ifPresent({ p ->
        logger.debug { "Stopping $name" }
        p.shutDown()
        logger.debug { "Deleting token for $name" }
        this.repository.deleteById(id)
        logger.debug { "Starting $name" }
        p.start()
      })
    } else {
      logger.warn { "Token not found for $name processor. No replay initiated." }
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
