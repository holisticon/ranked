package de.holisticon.ranked.command

import de.holisticon.ranked.extension.singleEventUpcaster
import de.holisticon.ranked.model.ImageUrl
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.user.UserSupplier
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Suppress("SpringKotlinAutowiring")
@Configuration
class EventUpcasterConfiguration {

  @Bean
  fun playerCreatedNullTo2(users: UserSupplier) = singleEventUpcaster(
    eventType = PlayerCreated::class,
    revisions = null to "2",
    converter = addImageUrl({ users(it).imageUrl})
  )
}

internal fun addImageUrl(imageUrlSupplier: (String)->ImageUrl): (Document) -> Document = {
  val userId = it.rootElement.element("userName").element("value").text

  val entry = DocumentHelper.createElement("imageUrl")
  entry.text = imageUrlSupplier(userId)
  it.rootElement.add(entry)
  it
}

