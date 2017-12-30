package de.holisticon.ranked.command.handler

import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component
import java.util.*

/**
 * Tracking [EventHandler] just to keep the log.
 */
@Component
@ProcessingGroup(LoggingEventHandler.NAME)
class LoggingEventHandler {

  companion object : KLogging() {
    const val NAME = "Logging"
  }

  // all this does is to trace()  everything that happens.
  @EventHandler
  fun on(event: Objects) {
    logger.info { "Match created ${event}" }
  }

}
