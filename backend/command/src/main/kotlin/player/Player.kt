@file:Suppress("UNUSED")

package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.*
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.properties.RankedProperties
import de.holisticon.ranked.service.user.UserService
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

  // create player aggregate when matchWinnerSaga receives matchCreated event
  // only called once for each player
  @CommandHandler
  constructor(c: CreatePlayer,
              @Autowired userService: UserService,
              @Autowired properties: RankedProperties) : this() {
    // get user data from ....
    val user = userService.loadUser(c.userName.value)
    val userName = UserName(user.id)
    val initialElo = properties.elo.default

    // -> #on(e: PlayerCreated)
    apply(
      PlayerCreated(
        userName = userName,
        displayName = user.name,
        imageUrl = user.imageUrl,
        initialElo = initialElo
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
    imageUrl = e.imageUrl
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

