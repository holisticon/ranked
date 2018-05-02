@file:Suppress("UNUSED")
package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateTeam
import de.holisticon.ranked.command.api.RenameTeam
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.TeamCreated
import de.holisticon.ranked.model.event.TeamRenamed
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class TeamAggregate() {

  @AggregateIdentifier
  lateinit var id: String
  lateinit var name: String
  lateinit var team: Team
  lateinit var imageUrl: String

  @CommandHandler
  constructor(c: CreateTeam): this() {

    apply(TeamCreated(
      id = c.id,
      name = c.name,
      team = c.team,
      imageUrl = c.imageUrl
    ))
  }

  @CommandHandler
  fun handle(c: RenameTeam) {
    if (name != c.newName) {
      apply(TeamRenamed(
        id = c.id,
        name = c.newName
      ))
    }
  }

  @EventSourcingHandler
  fun on(e: TeamCreated) {
    id = e.id
    name = e.name
    team = e.team
    imageUrl = e.imageUrl
  }

  @EventSourcingHandler
  fun on(e: TeamRenamed) {
    name = e.name
  }

}
