@file:Suppress("UNUSED")

package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatchSet
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
class MatchWinnerSaga {

  companion object : KLogging()

  @Autowired
  @Transient
  private lateinit var matchService: MatchService

  @Autowired
  @Transient
  private lateinit var commandGateway: CommandGateway

  private var teamWins: MutableMap<Team, Int> = mutableMapOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: MatchCreated) {
    // (2) a played match starts a match saga
    // TODO: why saga not via constructor?

    logger.trace("Match Winner Saga started for match ${e.matchId}")
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: TeamWonMatchSet) {
    // increase matchSet count for winner team
    val wins = teamWins.getOrDefault(e.team, 0).inc()
    teamWins.put(e.team, wins)

    // if this was the last set (one team won), fire event
    if (matchService.winsMatch(wins)) {
      // -> Match#on(c: WinMatch)
      // TODO: exception handling on fail.
      commandGateway.send<Any>(WinMatch(
        winner = e.team,
        looser = e.looser,
        matchId = e.matchId
      ))

      // this saga is ended, match won, "transaction" closed
      end()
    }
  }
}
