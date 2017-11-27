package de.holisticon.ranked.axon

import de.holisticon.ranked.axon.test.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.config.ProcessingGroup
import org.junit.Test

class TrackingProcessorSpec {


  @Test
  fun `get the name`() {
    assertThat(IsTrackingProcessor::class.trackingProcessor()).isEqualTo("IsTrackingProcessor")
  }

  @Test
  fun `name is null - no processingGroup`() {
    assertThat(NoProcessingGroup::class.trackingProcessor()).isNull()
  }

  @Test
  fun `no trackingProcessor`() {
    assertThat(NoTrackingProcessor::class.trackingProcessor()).isNull()
  }

  @Test
  fun `no annotations`() {
    assertThat(String::class.trackingProcessor()).isNull()
  }
}

