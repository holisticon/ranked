package de.holisticon.ranked

import de.holisticon.ranked.command.rest.CommandApi

import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) {
  SpringApplication.run(RankedApplication::class.java, *args)
}

@SpringBootApplication
class RankedApplication {

  // TODO why do we need this?
  @Bean
  fun commandApi(commandGateway: CommandGateway) = CommandApi(commandGateway)

}

@Configuration
@EnableSwagger2
class SwaggerConfiguration {
  /**
   * Swagger configuration
   */
  @Bean
  fun commandApi() = Docket(DocumentationType.SWAGGER_2)
    .select()
    .apis(RequestHandlerSelectors.any())
    .paths(PathSelectors.any()) // Predicates.not(PathSelectors.regex("/error"))
    .build()

  // TODO: had to remove this because it fails with 2.7.0 ... api changed
//    .apiInfo(ApiInfo(
//      "Ranked Command API",
//      "Command API to record new match results in ranked.",
//      "1.0.0",
//      "None",
//      Contact("Holisticon Craftsmen", "https://www.holisticon.de", "jobs@holisticon.de"),
//      "Revised BSD License", "https://github.com/holisticon/ranked/blob/master/LICENSE.txt"))

}
