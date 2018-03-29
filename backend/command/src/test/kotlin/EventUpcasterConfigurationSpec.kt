package de.holisticon.ranked.command

import de.holisticon.ranked.model.user.User
import org.assertj.core.api.Assertions.assertThat
import org.dom4j.io.SAXReader
import org.junit.Test
import java.io.StringReader


class EventUpcasterConfigurationSpec {

  private val kermit = User(id = "kermit", name = "Kermit TheFrog", imageUrl = "someUrl/kermit.jpg")

  private val playerCreatedXml = """
      <de.holisticon.ranked.model.event.PlayerCreated>
        <userName>
          <value>${kermit.id}</value>
        </userName>
        <displayName>${kermit.name}</displayName>
        <initialElo>1000</initialElo>
      </de.holisticon.ranked.model.event.PlayerCreated>
      """.trimIndent()

  @Test
  fun `addImageUrl adds imageUrl to PlayerCreated`() {
    val document = addImageUrl({kermit.imageUrl})(SAXReader().read(StringReader(playerCreatedXml)))
    assertThat(document.rootElement.element("imageUrl")?.text).isEqualTo("someUrl/kermit.jpg")
  }
}
