package de.holisticon.ranked.command

import de.holisticon.ranked.extension.singleEventUpcaster
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.service.user.UserService
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Suppress("SpringKotlinAutowiring")
@Configuration
class EventUpcasterConfiguration {

  @Bean
  fun playerCreatedNullTo2(userService: UserService) = singleEventUpcaster(
    eventType = PlayerCreated::class,
    oldRevision = null,
    newRevision = "2",
    converter = addImageUrl(userService))
}

internal fun addImageUrl(userService: UserService): (Document) -> Document = {
  val userId = it.rootElement.element("userName").element("value").text

  val entry = DocumentHelper.createElement("imageUrl")
  entry.text = userService.loadUser(userId).imageUrl
  it.rootElement.add(entry)
  it
}
