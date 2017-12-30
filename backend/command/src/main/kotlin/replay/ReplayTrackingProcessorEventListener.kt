package de.holisticon.ranked.command.replay

import de.holisticon.ranked.command.data.TokenJpaRepository
import de.holisticon.ranked.model.event.internal.ReplayTrackingProcessor
import mu.KLogging
import org.axonframework.config.EventHandlingConfiguration
import org.axonframework.eventhandling.EventProcessor
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Consumer

/**
 * Triggers replay for given trackingProcessor.
 */
@Component
class ReplayTrackingProcessorEventListener(
  val repository: TokenJpaRepository,
  val eventHandlingConfiguration: EventHandlingConfiguration
) : Consumer<ReplayTrackingProcessor> {

  companion object : KLogging() {
    // TODO: this is only 0 for the given trivial non ditributed setup
    const val SEGMENT = 0
  }

  @EventListener
  override fun accept(processingGroup: ReplayTrackingProcessor) {
    logger.info { "Replay requested: $processingGroup" }

    val id: TokenEntry.PK = TokenEntry.PK(processingGroup, SEGMENT)

    // TODO: replace optional behavior with elvis operator for repository.findByPk()
    val one: Optional<TokenEntry> = repository.findById(id)
    if (one.isPresent) {
      val processor: Optional<EventProcessor> = this.eventHandlingConfiguration.getProcessor(processingGroup)
      processor.ifPresent({ p ->
        logger.debug { "Stopping $processingGroup" }
        p.shutDown()
        logger.debug { "Deleting token for $processingGroup" }
        this.repository.deleteById(id)
        logger.debug { "Starting $processingGroup" }
        p.start()
      })
    } else {
      logger.warn{ "Token not found for $processingGroup processor. No replay initiated." }
    }
  }
}
