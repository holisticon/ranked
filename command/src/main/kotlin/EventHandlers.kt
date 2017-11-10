package de.holisticon.ranked.command.logging

import de.holisticon.ranked.axon.TrackingProcessor
import de.holisticon.ranked.command.event.MatchCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@TrackingProcessor
@Component
@ProcessingGroup("Logging")
class LoggingEventHandler {

  companion object: KLogging()

  @EventHandler
  fun on(event: MatchCreated) {
    logger.info { "Match created ${event.matchId}" }
  }

}
