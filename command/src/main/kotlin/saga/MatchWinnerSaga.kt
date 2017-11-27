package saga

import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.model.event.TeamWonMatchSet
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.util.concurrent.CompletableFuture

@Saga
class MatchWinnerSaga {

  @Autowired
  @Transient private lateinit var matchService: MatchService

  @Autowired
  @Transient private lateinit var commandGateway: CommandGateway

  private var teamWins: MutableMap<Team, Int> = mutableMapOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: MatchCreated) {

  }

  @SagaEventHandler(associationProperty = "matchId")
  fun handle(e: TeamWonMatchSet) {
    val wins = teamWins.getOrDefault(e.team, 0).inc()
    teamWins.put(e.team, wins)
    if (matchService.winsMatch(wins)) {
      val future: CompletableFuture<Any> = commandGateway.send(WinMatch(
        winner = e.team,
        looser = e.looser,
        matchId = e.matchId
      ))
      end()
    }

  }
}
