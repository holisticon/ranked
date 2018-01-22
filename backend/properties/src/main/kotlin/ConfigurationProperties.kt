package de.holisticon.ranked.properties

import de.holisticon.ranked.extension.validate
import mu.KLogging
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.Valid
import javax.validation.Validation
import javax.validation.ValidationException
import javax.validation.Validator

@ConfigurationProperties(prefix = "ranked")
data class RankedProperties(
  @field: Range(min = 1, max = 10)
  var scoreToWinSet: Int = 6,

  @field: Range(min = 1, max = 5)
  var setsToWinMatch: Int = 2,

  @NestedConfigurationProperty
  @field: Valid
  var elo: EloProperty = EloProperty()
)


data class EloProperty(

  @field: Range(min = 100, max = 2000)
  var default: Int = 1000,

  @field: Range(min = 1, max = 500)
  var maxDifference: Int = 400,

  @field: Range(min = 1, max = 100)
  var factor: Int = 20
)

/**
 * The spring boot auto-configuration. This is listed in META-INF/spring.factories, so it is
 * auto configured as soon as the ranked-properties module is on the classpath.
 */
@Configuration
@EnableConfigurationProperties(RankedProperties::class)
class RankedPropertiesAutoConfiguration {

  companion object : KLogging()

  @Autowired
  fun init(properties: RankedProperties) = logger.info { "starting with: $properties" }

  @Bean
  fun validatorFactoryBean(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()

}

fun createProperties(
  scoreToWinSet: Int = 6,
  setsToWinMatch: Int = 2,
  eloDefault: Int = 1000,
  eloMaxDifference: Int = 400,
  eloFactor: Int = 20,
  validator: Validator = Validation.buildDefaultValidatorFactory().validator
): RankedProperties {

  val p = RankedProperties(
    setsToWinMatch = setsToWinMatch,
    scoreToWinSet = scoreToWinSet,
    elo = EloProperty(default = eloDefault, maxDifference = eloMaxDifference, factor = eloFactor))

  val violations = p.validate(validator)
  if (!violations.isEmpty()) {
    throw ValidationException(violations.map { it.message }.joinToString())
  }

  return p;

}
