package de.holisticon.ranked.frontend

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner::class)
class SPARoutingITest {

  @Autowired
  lateinit var rest: TestRestTemplate

  @Test
  fun testClientStates() {
    assertThat(rest.getForEntity("/does-not-exist", Void::class.java).statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    assertThat(rest.getForEntity("/select", Void::class.java).statusCode).isEqualTo(HttpStatus.OK)
    assertThat(rest.getForEntity("/select/foo", Void::class.java).statusCode).isEqualTo(HttpStatus.OK)
    assertThat(rest.getForEntity("/board", Void::class.java).statusCode).isEqualTo(HttpStatus.OK)
  }
}
