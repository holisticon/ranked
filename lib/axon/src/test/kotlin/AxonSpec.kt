package de.holisticon.ranked.axon

import org.assertj.core.api.Assertions.assertThat
import org.axonframework.config.ProcessingGroup
import org.junit.Test


class AxonSpec {

  @Test
  fun `get the name`() {
    assertThat(trackingProcessorName(IsTrackingProcessor::class)).isEqualTo("IsTrackingProcessor")
  }

  @Test
  fun `name is null - no processingGroup`() {
    assertThat(trackingProcessorName(NoProcessingGroup::class)).isNull()
  }

  @Test
  fun `no trackingProcessor`() {
    assertThat(trackingProcessorName(NoTrackingProcessor::class)).isNull()
  }

  @Test
  fun `no annotations`() {
    assertThat(trackingProcessorName(String::class)).isNull()
  }
}

@TrackingProcessor
class NoProcessingGroup

@ProcessingGroup("NoTrackingProcessor")
class NoTrackingProcessor

@TrackingProcessor
@ProcessingGroup("IsTrackingProcessor")
class IsTrackingProcessor
