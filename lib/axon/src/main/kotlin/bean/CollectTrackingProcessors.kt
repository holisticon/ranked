package de.holisticon.ranked.axon.bean

import de.holisticon.ranked.axon.TrackingProcessors
import de.holisticon.ranked.axon.trackingProcessor
import de.holisticon.ranked.extension.getTargetClass
import de.holisticon.ranked.extension.inPackage
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

/**
 * Configures a Spring BeanPostProcessor that finds all beans annotated with
 * TrackingProcessor and ProcessingGroup and collects their group names in trackingProcessors.
 */
@Configuration
class CollectTrackingProcessors {
  /**
   * Mutable sets to collect during post processing.
   */
  private val trackingProcessorSet = mutableSetOf<String>()

  /**
   * Lazy constructor for immutable data object.
   */
  private val trackingProcessors: TrackingProcessors by lazy {
    TrackingProcessors(trackingProcessorSet)
  }

  @Bean
  fun scanForTrackingProcessors() = object : BeanPostProcessor {
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
      val targetClass = bean.getTargetClass()

      if (targetClass.inPackage("de.holisticon.ranked")) {
        bean.getTargetClass().trackingProcessor()?.let { trackingProcessorSet.add(it) }
      }

      return bean
    }
  }

  @Bean
  @Lazy
  fun trackingProcessor(): TrackingProcessors = trackingProcessors

}
