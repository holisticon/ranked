package de.holisticon.ranked.command.logging

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.model.event.MatchCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@TrackingProcessor
@Component
@ProcessingGroup("Logging")
class LoggingEventHandler {

  companion object : KLogging()

  @EventHandler
  fun on(event: MatchCreated) {
    // logger.trace { "Match created ${event.matchId}" }
  }

}
