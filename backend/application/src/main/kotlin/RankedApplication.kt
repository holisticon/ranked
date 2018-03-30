package de.holisticon.ranked

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<RankedApplication>(*args)

@SpringBootApplication
class RankedApplication

@Configuration
@EnableSwagger2
class SwaggerConfiguration {

  @Bean
  fun viewDocket() = docket(
    groupName = "Views",
    title = "Ranked View API",
    description = "View API to access different views in ranked.",
    basePackage = "de.holisticon.ranked.view",
    path = "/view/**")

  @Bean
  fun commandDocket() = docket(
    groupName = "Commands",
    title = "Ranked Command API",
    description = "Command API to write to ranked.",
    basePackage = "de.holisticon.ranked.command",
    path = "/command/**")

}


private fun docket(groupName: String, title: String, description: String, basePackage: String, path: String) = Docket(DocumentationType.SWAGGER_2)
  .groupName(groupName)
  .apiInfo(ApiInfoBuilder()
    .title(title)
    .description(description)
    .termsOfServiceUrl("None")
    .version("1.0.0")
    .contact(Contact("Holisticon Craftsmen", "https://www.holisticon.de", "jobs@holisticon.de"))
    .license("Revised BSD License")
    .licenseUrl("https://github.com/holisticon/ranked/blob/master/LICENSE.txt")
    .build())
  .select()
  .apis(RequestHandlerSelectors.basePackage(basePackage))
  .paths(PathSelectors.ant(path))
  .build()
