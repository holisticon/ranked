package de.holisticon.ranked.view.user

import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.user.User
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.stereotype.Component
import java.time.Instant

@ProcessingGroup(UserService.NAME)
@Component
class UserService {

  companion object : KLogging() {
    const val NAME = "User"
  }

  val users: MutableSet<User> = mutableSetOf()

  @EventHandler
  fun on(e: PlayerCreated, @Timestamp timestamp: Instant) {
    users.add(User(id = e.userName.value, name = e.displayName, imageUrl = e.imageUrl))
  }

}
