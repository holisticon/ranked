package de.holisticon.ranked.service.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.holisticon.ranked.model.user.User
import org.springframework.stereotype.Component

@Component
class PlayerImportService() {

  val users by lazy {
    readUsersFromJson("/players.json")
  }

  fun loadAll() = users
}

fun readUsersFromJson(resource: String): Set<User> = jacksonObjectMapper().readValue(PlayerImportService::class.java.getResource(resource))
