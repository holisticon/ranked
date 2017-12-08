package de.holisticon.ranked.command

import de.holisticon.ranked.axon.TrackingProcessors
import de.holisticon.ranked.command.rest.CommandApi
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.model.event.internal.ReplayTrackingProcessor
import de.holisticon.ranked.properties.RankedProperties
import mu.KLogging
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*
import javax.validation.ValidatorFactory

/**
 * Configure components.
 */
@Configuration
@EnableSwagger2
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

  // TODO why do we need this?

  @Bean
  fun command(commandGateway: CommandGateway) = CommandApi(commandGateway)

  @Bean
  fun validatorFactoryBean(): ValidatorFactory = LocalValidatorFactoryBean()

//  @Bean
//  fun matchService(properties: RankedProperties) = MatchService(properties)

//   @Bean
//  fun userService() = UserService()

  /**
   * Swagger configuration
   */
  @Bean
  fun commandApi(): Docket = Docket(DocumentationType.SWAGGER_2)
    .groupName("Commands")
    .select()
    .apis(RequestHandlerSelectors.basePackage(CommandApi::class.java.`package`.name))
    .paths(PathSelectors.ant("/command/**"))
    .build()
    .apiInfo(ApiInfo(
      "Ranked Command API",
      "Command API to record new match results in ranked.",
      "1.0.0",
      "None",
      Contact("Holisticon Craftsmen", "https://www.holisticon.de", "jobs@holisticon.de"),
      "Revised BSD License",
      "https://github.com/holisticon/ranked/blob/master/LICENSE.txt",
      ArrayList()))
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
  val properties : RankedProperties
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
      logger.info { "Token not found for $name processor. No replay initiated." }
    }
  }

  fun replayAll() = trackingProcessors.forEach { name -> replay(ReplayTrackingProcessor(name)) }

}
