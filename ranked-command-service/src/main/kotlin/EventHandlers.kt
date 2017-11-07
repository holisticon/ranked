package de.holisticon.ranked.view.matches

import de.holisticon.ranked.command.axon.TrackingProcessor
import de.holisticon.ranked.command.event.MatchCreated
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@TrackingProcessor
@Component
@ProcessingGroup("Matches")
class LoggingEventHandler() {

  companion object: KLogging()

  @EventHandler
  fun on(event: MatchCreated) {
    logger.info { "Match created ${event.matchId}" }
  }

}
