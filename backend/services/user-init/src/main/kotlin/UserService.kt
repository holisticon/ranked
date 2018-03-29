package de.holisticon.ranked.service.user

import de.holisticon.ranked.model.user.User
import org.springframework.stereotype.Component

@Component
@Deprecated("we do not use UserService anymore, just the UserInitialization")
class UserService {

  private val users by lazy {
    readUsersFromJson("/players.json")
  }

  fun loadUser(username: String): User = users.find { username == it.id }!!

  fun loadAll() = users
}

