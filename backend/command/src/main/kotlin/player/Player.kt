@file:Suppress("UNUSED")

package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.*
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.properties.RankedProperties
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired

@Aggregate
class Player() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var userName: UserName
  private lateinit var displayName: String
  private var eloRanking: Int = 0
  private var imageUrl: String = ""
  private var participatingInMatchId: String = ""

  @CommandHandler
  constructor(c: CreatePlayerAndUser,
              @Autowired properties: RankedProperties) : this() {
    apply(
      PlayerCreated(
        userName = c.userName,
        displayName = c.displayName,
        initialElo = properties.elo.default,
        imageUrl = c.imageUrl
      )
    )
  }


  @CommandHandler
  fun handle(c: ParticipateInMatch) {

    if (isInMatch()) {
      throw IllegalStateException("Current player is already participating in match " + this.participatingInMatchId)
    }

    // inform the world about current elo
    apply(PlayerParticipatedInMatch(
      player = c.userName,
      matchId = c.matchId,
      eloRanking = eloRanking
    ))
  }

  @CommandHandler
  fun handle(c: CancelParticipation) {
    if (isInMatch()) {
      apply(ParticipationCancelled(this.userName))
    }
  }

  @CommandHandler
  fun handle(c: UpdatePlayerRanking) {
    apply(PlayerRankingChanged(
      player = c.userName,
      eloRanking = c.eloRanking
    ))
  }

  @CommandHandler
  fun handle(c: CheckPlayer) {
    apply(PlayerExists(c.userName))
  }


  @EventSourcingHandler
  fun on(e: PlayerCreated) {
    userName = e.userName
    displayName = e.displayName
    eloRanking = e.initialElo
  }

  @EventSourcingHandler
  fun on(e: PlayerRankingChanged) {
    logger.trace { "Elo ranking changed for $displayName from $eloRanking to ${e.eloRanking}" }
    eloRanking = e.eloRanking
    resetParticipation()
  }


  @EventSourcingHandler
  fun on(e: PlayerParticipatedInMatch) {
    participatingInMatchId = e.matchId
  }

  @EventSourcingHandler
  fun on(e: ParticipationCancelled) {
    resetParticipation()
  }


  fun isInMatch() = this.participatingInMatchId != ""
  fun resetParticipation() {
    this.participatingInMatchId = ""
  }
}

