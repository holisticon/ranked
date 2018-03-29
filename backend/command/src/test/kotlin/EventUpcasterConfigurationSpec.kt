package de.holisticon.ranked.command

import de.holisticon.ranked.model.user.User
import de.holisticon.ranked.service.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.dom4j.io.SAXReader
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
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

  private val userService = mock(UserService::class.java)!!

  @Test
  fun `addImageUrl adds imageUrl to PlayerCreated`() {
    `when`(userService.loadUser(kermit.id)).thenReturn(kermit)

    val document = addImageUrl(userService)(SAXReader().read(StringReader(playerCreatedXml)))

    assertThat(document.rootElement.element("imageUrl")?.text).isEqualTo("someUrl/kermit.jpg")
  }
}
