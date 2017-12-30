package de.holisticon.ranked.properties

import de.holisticon.ranked.properties.test.PropertiesSpecApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [PropertiesSpecApplication::class])
class PropertiesSpec {

  @Autowired
  private lateinit var properties: RankedProperties

  @Test
  fun `initialize properties with default values`() {
    assertThat(properties.elo.default).isEqualTo(1000)
    assertThat(properties.scoreToWinSet).isEqualTo(6)
    assertThat(properties.setsToWinMatch).isEqualTo(2)

  }
}
