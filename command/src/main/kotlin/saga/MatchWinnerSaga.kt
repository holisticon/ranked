package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatchSet
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

@Saga
@Suppress("UNUSED")
class MatchWinnerSaga {

  companion object: KLogging()

  @Autowired
  @Transient
  private lateinit var matchService: MatchService

  @Autowired
  @Transient
  private lateinit var commandGateway: CommandGateway

  private var teamWins: MutableMap<Team, Int> = mutableMapOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: MatchCreated) {
    // (2) a played match starts a match saga
    // TODO: why saga not via constructor?

    logger.info("Saga started for match ${e.matchId}")


    // key/value map inside saga context, just keep the player ids
    associateWith("bluePlayer1", e.teamBlue.player1.value)
    associateWith("bluePlayer2", e.teamBlue.player2.value)
    associateWith("redPlayer1", e.teamRed.player1.value)
    associateWith("redPlayer2", e.teamRed.player2.value)

    // FIXME: don't create any players from here. not deleted to discuss.
    // create players (-> Player), so players exists when win/loose is calculated
    // val users = arrayOf(e.teamBlue.player1, e.teamBlue.player2, e.teamRed.player1, e.teamRed.player2)
    // users.iterator().forEach{user -> commandGateway.send<Any>(CreatePlayer(userName = user))}
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: TeamWonMatchSet) {
    // increase matchSet count for winner team
    val wins = teamWins.getOrDefault(e.team, 0).inc()
    teamWins.put(e.team, wins)

    // if this was the last set (one team won), fire event
    if (matchService.winsMatch(wins)) {
      // -> Match#on(c: WinMatch)
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
