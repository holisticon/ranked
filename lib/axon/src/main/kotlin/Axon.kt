package de.holisticon.ranked.axon

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import kotlin.reflect.KClass


/**
 * Marker annotation for tracking Axon processors, which will be registered on startup.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TrackingProcessor

fun trackingProcessorName(beanClass: KClass<*>): String? {
  val tp = beanClass.annotations.find { it is TrackingProcessor }
  val pg = beanClass.annotations.find { it is ProcessingGroup } as ProcessingGroup?

  return if (tp != null) pg?.value else null
}

@Configuration
class TrackingProcessorConfiguration {

  companion object : KLogging()

  val trackingProcessorSet = mutableSetOf<String>()

  @Bean
  fun scanForTrackingProcessors() = object : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
      val targetClass = AopUtils.getTargetClass(bean);
      if (targetClass.`package`.name.startsWith("de.holistion")) {
        val name: String? = trackingProcessorName(targetClass.kotlin)
        logger.info { "processing: $bean, $beanName - $name" }
        if (name != null) trackingProcessorSet.add(name)
      }
      return bean
    }
  }

  @Bean
  @Lazy
  @Qualifier("trackingProcessors")
  fun trackingProcessor(): Set<String> = HashSet(trackingProcessorSet)

}
