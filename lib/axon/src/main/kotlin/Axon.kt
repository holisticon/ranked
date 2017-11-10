package de.holisticon.ranked.axon


/**
 * Marker annotation for tracking Axon processors, which will be registered on startup.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TrackingProcessor

