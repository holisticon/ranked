package de.holisticon.ranked.command

import de.holisticon.ranked.command.axon.TrackingProcessorService
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration

/**
 * The spring boot main application.
 */
@SpringBootApplication
class CommandServiceApplication

fun main(args: Array<String>) {
  SpringApplication.run(CommandServiceApplication::class.java, *args)
}

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
    trackingProcessorService.startReplay()
  }

}
