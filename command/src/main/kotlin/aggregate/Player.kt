package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate


@Aggregate
@Suppress("UNUSED")
class Player() {

  @AggregateIdentifier
  private lateinit var userName: UserName
  private lateinit var displayName: String

  @CommandHandler
  constructor(c: CreatePlayer) : this() {
    AggregateLifecycle.apply(
      PlayerCreated(
        userName = c.userName,
        displayName = c.displayName,
        date = c.date
      )
    )
  }

  @EventSourcingHandler
  fun on(e: PlayerCreated) {
    userName = e.userName
    displayName = e.displayName
  }
}
