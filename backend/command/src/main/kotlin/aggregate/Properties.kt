package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CheckConfiguration
import de.holisticon.ranked.command.api.CreateConfiguration
import de.holisticon.ranked.command.api.UpdateConfiguration
import de.holisticon.ranked.properties.RankedProperties
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate


data class ConfigurationCreated(
  val id:String,
  val properties: RankedProperties
)

data class ConfigurationUpdated(
  val id:String,
  val properties: RankedProperties
)

data class ConfigurationExists(
  val id: String
)

@Aggregate
@Suppress("UNUSED")
class Properties() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var id: String

  private lateinit var rankedProperties : RankedProperties

  @CommandHandler
  constructor(c: CreateConfiguration) : this() {
    apply(ConfigurationCreated(c.id, c.properties))
  }

  @CommandHandler
  fun handle(c: UpdateConfiguration) {
    if (rankedProperties != c.properties) {
      apply(ConfigurationUpdated(c.id, c.properties))
    }
  }

  @CommandHandler
  fun handle(c: CheckConfiguration) {
    apply(ConfigurationExists(id))
  }

  @EventSourcingHandler
  fun on(e: ConfigurationCreated) {
    logger.info { "configuration created: $rankedProperties" }
    this.id = e.id
    this.rankedProperties = e.properties
  }

  @EventSourcingHandler
  fun on(e: ConfigurationUpdated) {
    logger.info { "configuration updated: new=${e.properties}, old=$rankedProperties" }
    this.id = e.id
    this.rankedProperties = e.properties
  }
}
