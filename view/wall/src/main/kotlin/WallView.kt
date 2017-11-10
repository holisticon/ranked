package de.holisticon.ranked.view.wall

import de.holisticon.ranked.axon.TrackingProcessor
import org.axonframework.config.ProcessingGroup
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
@TrackingProcessor
@ProcessingGroup("Wall")
class MatchHandler {

}
