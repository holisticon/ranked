package de.holisticon.ranked.extension

import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.TrackingEventProcessor
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import kotlin.reflect.KClass

/**
 * Tuple of oldRevision (nullable) and newRevision.
 */
typealias Revisions = Pair<String?, String>

/**
 * Creates a singleEventUpcaster for given type and revisions and calls [IntermediateEventRepresentation#upcastPayload] using the [converter].
 */
fun singleEventUpcaster(eventType: KClass<*>,
                        revisions: Revisions,
                        converter: (Document) -> Document): SingleEventUpcaster = object : SingleEventUpcaster() {

  override fun canUpcast(ir: IntermediateEventRepresentation): Boolean = SimpleSerializedType(eventType.qualifiedName, revisions.first) == ir.type

  override fun doUpcast(ir: IntermediateEventRepresentation): IntermediateEventRepresentation =
    ir.upcastPayload(
      SimpleSerializedType(eventType.qualifiedName, revisions.second),
      Document::class.java,
      converter)
}


/**
 * Simplifies sending commands with callback to the CommandGateway.
 */
inline fun <C, R> CommandGateway.send(command: C,
                                      crossinline success: (CommandMessage<out C>, R) -> Unit = { _, _: R -> },
                                      crossinline failure: (CommandMessage<out C>, Throwable) -> Unit = { _, _: Throwable -> }
) = this.send(command, object : CommandCallback<C, R> {
  override fun onSuccess(commandMessage: CommandMessage<out C>, result: R) {
    success(commandMessage, result)
  }

  override fun onFailure(commandMessage: CommandMessage<out C>, cause: Throwable) {
    failure(commandMessage, cause)
  }
})

/**
 * Access all tracking processors.
 */
fun EventHandlingConfiguration.trackingEventProcessors() =  this.processors.filter { it is TrackingEventProcessor }.map { it as TrackingEventProcessor }
