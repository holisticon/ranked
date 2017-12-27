package de.holisticon.ranked.command.handler

import de.holisticon.ranked.model.event.MatchCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

/**
 * Tracking [EventHandler] just to keep the log.
 */
@Component
@ProcessingGroup("Logging")
class LoggingEventHandler {

  companion object : KLogging()

  @EventHandler
  fun on(event: MatchCreated) {
    logger.trace { "Match created ${event.matchId}" }
  }

}
