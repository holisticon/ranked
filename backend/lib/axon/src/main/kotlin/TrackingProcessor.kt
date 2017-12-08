package de.holisticon.ranked.axon

import org.axonframework.config.ProcessingGroup
import kotlin.reflect.KClass

/**
 * Marker annotation for tracking Axon processors, which will be registered on startup.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TrackingProcessor

/**
 * Gets name of tracking processor from kotlin-class
 */
fun KClass<out Any>.trackingProcessor() : String? {
  val tp = this.annotations.find { it is TrackingProcessor }
  val pg = this.annotations.find { it is ProcessingGroup } as ProcessingGroup?

  return if (tp != null) pg?.value else null
}

/**
 * Stores a set of TrackingProcessor names in an internal set and provides
 * all Set functions via implements with delegation (by list).
 */
data class TrackingProcessors(private val list: Set<String>) : Set<String> by list
