package de.holisticon.ranked.service.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.holisticon.ranked.model.user.User
import org.springframework.stereotype.Component

@Component
class UserService() {

  val users by lazy {
    readUsersFromJson("/players.json")
  }

  fun loadUser(username: String): User = users.find { username == it.id }!!

  fun loadAll() = users
}

fun readUsersFromJson(resource: String): Set<User> = jacksonObjectMapper().readValue(UserService::class.java.getResource(resource))
