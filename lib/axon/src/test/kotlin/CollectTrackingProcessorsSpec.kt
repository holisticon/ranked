package de.holisticon.ranked.axon.bean

import de.holisticon.ranked.axon.TrackingProcessors
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@ContextConfiguration(classes = arrayOf(
  CollectTrackingProcessors::class, CollectTrackingProcessorsSpec.TestConfig::class
))
class CollectTrackingProcessorsSpec {

  @ComponentScan(basePackages = arrayOf("de.holisticon.ranked.axon.test"))
  class TestConfig

  @Autowired
  lateinit var ctx: ApplicationContext

  @Test
  fun `scans classes in package and return single name`() {
    val trackingProcessors = ctx.getBean(TrackingProcessors::class.java)

    assertThat(trackingProcessors.size).isEqualTo(1)
    assertThat(trackingProcessors.single()).isEqualTo("IsTrackingProcessor")
  }
}

