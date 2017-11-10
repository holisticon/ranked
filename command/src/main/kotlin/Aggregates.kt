package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.cmd.CreateMatch
import de.holisticon.ranked.command.cmd.CreatePlayer
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerCreated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Player() {

  @AggregateIdentifier
  private lateinit var userName: UserName
  private lateinit var displayName: String

  @CommandHandler
  constructor(c: CreatePlayer) : this() {
    apply(
      PlayerCreated(
        userName = c.userName,
        displayName = c.displayName
      )
    )
  }

  @EventSourcingHandler
  fun on(e: PlayerCreated) {
    userName = e.userName
    displayName = e.displayName
  }
}

@Aggregate
class Match() {

  @AggregateIdentifier
  private lateinit var matchId: String

  @CommandHandler
  constructor(c: CreateMatch) : this() {
    apply(
      MatchCreated(
        matchId = c.matchId,
        teamBlue = c.teamBlue,
        teamRed = c.teamRed,
        date = c.date,
        matchSets = c.matchSets,
        tournamentId = c.tournamentId
      )
    )
  }

  @EventSourcingHandler
  fun on(e: MatchCreated) {
    this.matchId = e.matchId
  }
}
