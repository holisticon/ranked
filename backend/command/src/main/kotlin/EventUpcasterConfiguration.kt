package de.holisticon.ranked.command

import de.holisticon.ranked.extension.singleEventUpcaster
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.service.user.UserService
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

