package de.holisticon.ranked.properties

import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.validation.annotation.Validated

// this is actually a whole lot of work to do for getting the yaml values as immutable bean.
// but at least, it does the job
// this will be obsolete (hopefully) because with spring boot 2 RC1 there will come a property binder for kotlin data classes


/**
 * Interface that defines the configuration contract, all these values have to be provided.
 */
interface IRankedProperties {
  val scoreToWinSet: Int
  val setsToWinMatch: Int
  val defaultElo: Int
}

/**
 * Intermediate, mutable implementation of IRankedProperties to allow linking it to
 * ssprings ConfigurationProperties.
 */
@ConfigurationProperties(prefix = "ranked")
@Validated
open class RankedConfigurationProperties : IRankedProperties {
  @get: Range(min = 1, max= 10)
  override var scoreToWinSet: Int = -1
  @get: Range(min = 1, max = 5)
  override var setsToWinMatch: Int = -1
  @get: Range(min = 1)
  override var defaultElo: Int = -1
}

/**
 * The actual data class that will get exposed as a bean. Uses kotlins internal delegation "by".
 */
data class RankedProperties(private val properties: RankedConfigurationProperties) : IRankedProperties by properties

/**
 * The spring boot auto-configuration. This is listed in META-INF/spring.factories, so it is
 * auto configured as soon as the ranked-properties module is on the classpath.
 */
@EnableConfigurationProperties(RankedConfigurationProperties::class)
class RankedPropertiesAutoConfiguration {

  // get access to the configurationProperties parsed by spring boot
  @Autowired
  private lateinit var configurationProperties: RankedConfigurationProperties

  // provide conversion to data class. Lazy: instance is only created once.
  private val properties : RankedProperties by lazy {
    RankedProperties(configurationProperties)
  }

  // expose the immutable properties as spring bean.
  @Bean
  fun rankedProperties(): RankedProperties = properties
}
