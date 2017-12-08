package de.holisticon.ranked.axon.test

import de.holisticon.ranked.axon.TrackingProcessor
import org.axonframework.config.ProcessingGroup
import org.springframework.stereotype.Component

@Component
@TrackingProcessor
class NoProcessingGroup

@Component
@ProcessingGroup("NoTrackingProcessor")
class NoTrackingProcessor

@Component
@TrackingProcessor
@ProcessingGroup("IsTrackingProcessor")
class IsTrackingProcessor
