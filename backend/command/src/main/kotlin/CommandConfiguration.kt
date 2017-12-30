package de.holisticon.ranked.command

import de.holisticon.ranked.command.replay.ReplayTrackingProcessorEventListener
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import mu.KLogging
import org.axonframework.boot.EventProcessorProperties
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.annotation.PostConstruct
import javax.validation.ValidatorFactory

/**
 * This is the main spring configuration class for everything axon/command related.
 */
@Configuration
class CommandConfiguration(
  val eventHandlingConfiguration: EventHandlingConfiguration,
  val eventProcessorProperties: EventProcessorProperties
) {

  companion object : KLogging() {
    const val PHASE_REPLAY = Int.MAX_VALUE - 10
  }

  private val trackingProcessors : MutableSet<String> = mutableSetOf()

  @PostConstruct
  fun init() {
    eventProcessorProperties.processors
      .map{Pair(it.key, it.value.mode)}
      .filter { it.second== EventProcessorProperties.Mode.TRACKING }
      .forEach{trackingProcessors.add(it.first) }
  }

  /**
   * Enable bean validation for incoming commands.
   */
  @Autowired
  fun configure(bus: SimpleCommandBus, validationFactory: ValidatorFactory) {
    bus.registerDispatchInterceptor(BeanValidationInterceptor(validationFactory))
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
  fun replayTrackingProcessors(replayTrackingProcessorEventListener: ReplayTrackingProcessorEventListener) = object : DefaultSmartLifecycle(PHASE_REPLAY) {
    override fun onStart() {
      eventHandlingConfiguration.processors
        .filter { trackingProcessors.contains(it.name) }
        .forEach {
          replayTrackingProcessorEventListener.accept(it.name)
        }
    }
  }
}
