@file:Suppress("UNUSED")

package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.command.service.UserService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.PlayerExists
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.PlayerRankingChanged
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

  // create player aggregate when matchWinnerSaga receives matchCreated event
  // only called once for each player
  @CommandHandler
  constructor(c: CreatePlayer,
              @Autowired userService: UserService,
              @Autowired properties: RankedProperties) : this() {
    // get user data from ....
    val user = userService.findUser(c.userName.value)

    // -> #on(e: PlayerCreated)
    apply(
      PlayerCreated(
        userName = UserName(user.userName),
        displayName = user.displayName,
        initialElo = properties.elo.default
      )
    )
  }

  @CommandHandler
  fun handle(c: ParticipateInMatch) {
    // TODO: validate if we are already in match

    // inform the world about current elo
    apply(PlayerParticipatedInMatch(
      player = c.userName,
      matchId = c.matchId,
      eloRanking = eloRanking
    ))
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
    logger.trace { "Elo ranking changed for ${displayName} from ${eloRanking} to ${e.eloRanking}" }
    eloRanking = e.eloRanking
  }

}

