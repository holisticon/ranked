import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.holisticon.ranked.model.TeamColor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.time.LocalDateTime

class JacksonSerializationTest {

  @Test
  fun `serialize pair with enum and date`() {
    val obj = Pair(TeamColor.RED, LocalDateTime.now())
    val mapper = ObjectMapper()
      .registerModule(JavaTimeModule())
      .registerModule(KotlinModule())
    val json = mapper.writer().writeValueAsString(obj)
    assertThat(json).isEqualTo("{\"first\":\"RED\",\"second\":[2018,1,19,21,20,42,509000000]}")
  }
}

