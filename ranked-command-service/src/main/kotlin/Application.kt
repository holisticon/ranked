package de.holisticon.ranked.command

import de.holisticon.ranked.command.axon.TrackingProcessorService
import org.axonframework.commandhandling.SimpleCommandBus
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

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

@Configuration
@EnableSwagger2
class SwaggerConfiguration {
  /**
   * Swagger configuration
   */
  @Bean
  fun commandApi() : Docket {
    return Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.any())
      .paths(PathSelectors.any()) // Predicates.not(PathSelectors.regex("/error"))
      .build()
      .apiInfo(ApiInfo(
        "Ranked Command API",
        "Command API to record new match results in ranked.",
        "1.0.0",
        "None",
        Contact("Holisticon Craftsmen", "https://www.holisticon.de", "jobs@holisticon.de"),
        "Revised BSD License", "https://github.com/holisticon/ranked/blob/master/LICENSE.txt"))
  }
}

