package de.holisticon.ranked.service.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.holisticon.ranked.extension.defaultSmartLifecycle
import de.holisticon.ranked.model.event.internal.InitUser
import de.holisticon.ranked.model.user.User
import de.holisticon.ranked.model.user.UserSupplier
import mu.KLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class UserInitializationConfiguration {
  companion object : KLogging()

  private val users by lazy {
    readUsersFromJson("/players.json")
  }

  @Bean
  @Profile("!itest")
  fun initializeUsers(publisher: ApplicationEventPublisher) = defaultSmartLifecycle(Int.MAX_VALUE - 20) {
    users.forEach {
      val initUser: InitUser = it
      logger.debug { "initUser from json: $initUser" }
      publisher.publishEvent(initUser)
    }
  }

  @Bean
  fun users(): UserSupplier = { id -> users.find { id == it.id }!! }
}


internal fun readUsersFromJson(resource: String): Set<User> = jacksonObjectMapper().readValue(UserInitializationConfiguration::class.java.getResource(resource))
