package de.holisticon.ranked.command.axon

import mu.KLogging
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import java.lang.annotation.Documented
import java.util.*
import java.util.stream.Collectors


/**
 * Token JPA repository
 */
interface TokenJpaRepository : JpaRepository<TokenEntry, TokenEntry.PK>


/**
 * Marker annotation for tracking Axon processors, which will be registered on startup.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TrackingProcessor


@Component
class TrackingProcessorService(val eventHandlingConfiguration: EventHandlingConfiguration, val repository: TokenJpaRepository) {

  companion object: KLogging()

  fun registerTrackingProcessors() {
    trackingProcessors().forEach { name ->
      logger.info{"Registering tracking processor $name."}
      eventHandlingConfiguration.registerTrackingProcessor(name)
    }
  }

  fun startReplay() {
    trackingProcessors().forEach { name ->
      val id: TokenEntry.PK = TokenEntry.PK(name, 0)
      val one : TokenEntry = repository.getOne(id)
      if (one == null) {
        logger.warn{"Token not found for $name processor."}
      } else {
        val processor: Optional<EventProcessor> = this.eventHandlingConfiguration.getProcessor(name)
        processor.ifPresent({ p ->
          logger.debug{"Stopping $name"}
          p.shutDown()
          logger.debug{"Deleting token for $name"}
          this.repository.deleteById(id)
          logger.debug{"Starting $name"}
          p.start()
        })
      }
    }
  }

  internal fun trackingProcessors() : List<String> {
    val scanner = ClassPathScanningCandidateComponentProvider(false)
    scanner.addIncludeFilter(AnnotationTypeFilter(TrackingProcessor::class.java))
    return scanner.findCandidateComponents("de.holisticon.ranked").stream()
      .map { bd ->
        Optional.ofNullable(classForBeanDefinition(bd).getAnnotation(ProcessingGroup::class.java))
          .map(ProcessingGroup::value)
          .orElse(classForBeanDefinition(bd).`package`.name)}
        .collect(Collectors.toList())
  }

  internal fun classForBeanDefinition(bd: BeanDefinition) : Class<*> {
    return Class.forName(bd.beanClassName)
  }


}
