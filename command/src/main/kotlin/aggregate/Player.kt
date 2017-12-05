package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.RankedProperties
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.service.UserService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired


@Aggregate
@Suppress("UNUSED")
class Player() {

  @AggregateIdentifier
  private lateinit var userName: UserName
  private lateinit var displayName: String
  private lateinit var eloRanking: Integer

  // create player aggregate when matchWinnerSaga receives matchCreated event
  // only called once for each player
  @CommandHandler
  constructor(c: CreatePlayer,
              @Autowired userService: UserService,
              @Autowired properties: RankedProperties) : this() {
    // get user data from ....
    val user = userService.findUser(c.userName.value)
    // TODO: what happens if user is not found (unlikely!)
    if (user != null) {
      // -> #on(e: PlayerCreated)
      apply(
        PlayerCreated(
          userName = UserName(user.userName),
          displayName = user.displayName,
          initialElo = properties.defaultElo
        )
      )
    }
  }

  @EventSourcingHandler
  fun on(e: PlayerCreated) {
    userName = e.userName
    displayName = e.displayName
    eloRanking = Integer(e.initialElo)
  }


}
