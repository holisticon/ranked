package saga

import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.command.service.UserService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.model.event.TeamWonMatchSet
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.associateWith
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.util.concurrent.CompletableFuture

@Saga
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

    logger.info("Saga started")

    /*
    associateWith("bluePlayer1", e.teamBlue.player1.value)
    associateWith("bluePlayer2", e.teamBlue.player2.value)
    associateWith("redPlayer1", e.teamRed.player1.value)
    associateWith("redPlayer2", e.teamRed.player2.value)
    */
    val users = arrayOf(e.teamBlue.player1, e.teamBlue.player2, e.teamRed.player1, e.teamRed.player2)
    users.iterator().forEach{user -> commandGateway.send<Any>(CreatePlayer(userName = user))}
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: TeamWonMatchSet) {
    val wins = teamWins.getOrDefault(e.team, 0).inc()
    teamWins.put(e.team, wins)
    if (matchService.winsMatch(wins)) {
      commandGateway.send<Any>(WinMatch(
        winner = e.team,
        looser = e.looser,
        matchId = e.matchId
      ))
      end()
    }
  }
}
