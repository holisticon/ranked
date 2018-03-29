package de.holisticon.ranked.extension

import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.dom4j.Document
import kotlin.reflect.KClass

fun singleEventUpcaster(eventType: KClass<*>, oldRevision: String?, newRevision: String,  converter: (Document)->Document): SingleEventUpcaster = object : SingleEventUpcaster() {

  override fun canUpcast(ir: IntermediateEventRepresentation): Boolean = SimpleSerializedType(eventType.qualifiedName, oldRevision) == ir.type

  override fun doUpcast(ir: IntermediateEventRepresentation): IntermediateEventRepresentation =
    ir.upcastPayload(
      SimpleSerializedType(eventType.qualifiedName, newRevision),
      Document::class.java,
      converter)

}
