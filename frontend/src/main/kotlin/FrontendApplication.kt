package de.holisticon.ranked.frontend

import de.holisticon.ranked.extension.runApplicationExpr
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver


/**
 * Main application -> run this!
 */
fun main(args: Array<String>) = runApplicationExpr<FrontendApplication>(*args)

@SpringBootApplication
@EnableZuulProxy
class FrontendApplication

object FrontendMetadata {
  val FRONTEND_ROUTES: Array<String> = arrayOf("/", "select", "/select/**", "/board", "tournament", "selectMatch", "/teamBoard", "/selectTeam", "/seacon", "/tournamentAdmin", "/tournamentAdmin/**", "/profile/**")
}

@Configuration
class SPAConfig : WebMvcConfigurer {


  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    /*
     * add resource mappings to support client states (routing in HTML5 mode)
     */
    registry
      .addResourceHandler(*FrontendMetadata.FRONTEND_ROUTES)
      .addResourceLocations("classpath:/static/index.html")
      .resourceChain(true)
      .addResolver(ResourcePathResolver())
    /*
     * Deliver static resources.
     */
    registry
      .addResourceHandler("/**")
      .addResourceLocations("classpath:/static/")
      .resourceChain(true)
  }
}

class ResourcePathResolver : PathResourceResolver() {
  override fun getResource(resourcePath: String, location : Resource) : Resource? {
    return if (location.exists() && location.isReadable) { location } else { null }
  }
}
