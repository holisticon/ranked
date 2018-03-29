package de.holisticon.ranked.service.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.holisticon.ranked.extension.defaultSmartLifecycle
import de.holisticon.ranked.model.event.internal.InitUser
import de.holisticon.ranked.model.user.User
import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class UserInitializationConfiguration(private val publisher: ApplicationEventPublisher) {
  companion object : KLogging()

  @Bean
  @Profile("!itest")
  fun initializeUsers() = defaultSmartLifecycle(Int.MAX_VALUE - 20) {
    readUsersFromJson("/players.json").forEach {
      val initUser: InitUser = it
      logger.info { "initUser from json: $initUser" }
      publisher.publishEvent(initUser)
    }
  }
}


fun readUsersFromJson(resource: String): Set<User> = jacksonObjectMapper().readValue(UserInitializationConfiguration::class.java.getResource(resource))
