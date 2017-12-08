package de.holisticon.ranked.properties

import de.holisticon.ranked.extension.validate
import mu.KLogging
import org.hibernate.validator.constraints.Range
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import javax.validation.*

// this is actually a whole lot of work to do for getting the yaml values as immutable bean.
// but at least, it does the job
// this will be obsolete (hopefully) because with spring boot 2 RC1 there will come a property binder for kotlin data classes


data class EloProperty(

  @get: Range(min = 100, max = 2000)
  val default: Int = 1000,

  @get: Range(min = 1, max = 500)
  val maxDifference: Int = 400,

  @get: Range(min = 1, max = 100)
  val factor: Int = 20
)


data class RankedProperties(
  @get: Range(min = 1, max = 10)
  val scoreToWinSet: Int = 6,

  @get: Range(min = 1, max = 5)
  val setsToWinMatch: Int = 2,

  @get: Valid
  val elo: EloProperty = EloProperty()
)

/**
 * The spring boot auto-configuration. This is listed in META-INF/spring.factories, so it is
 * auto configured as soon as the ranked-properties module is on the classpath.
 */
@Configuration
class RankedPropertiesAutoConfiguration {

  companion object : KLogging()

  @Bean
  fun validatorFactoryBean(): LocalValidatorFactoryBean = LocalValidatorFactoryBean()

  @Bean
  fun properties(
    @Value("\${ranked.scoreToWinSet}") scoreToWinSet : Int,
    @Value("\${ranked.setsToWinMatch}") setsToWinMatch : Int,
    @Value("\${ranked.elo.default}") eloDefault : Int,
    @Value("\${ranked.elo.maxDifference}") eloMaxDifference : Int,
    @Value("\${ranked.elo.factor}") eloFactor: Int
  ) : RankedProperties {
    val properties = createProperties(scoreToWinSet = scoreToWinSet, setsToWinMatch = setsToWinMatch, eloDefault = eloDefault, eloMaxDifference = eloMaxDifference, eloFactor = eloFactor)

    logger.info{ "staring with properties: ${properties}" }

    return properties
  }
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
