package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.command.service.EloCalculationService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.TeamWonMatch
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired


@Saga
class EloMatchSaga {

  companion object: KLogging() {
    val UNSET_ELO = -1
  }

  @Autowired
  @Transient private val commandGateway: CommandGateway? = null

  @Autowired
  @Transient private val eloCalculationService: EloCalculationService? = null

  private val rankings: MutableMap<UserName, Int> = mutableMapOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: MatchCreated) {

    logger.info("Elo Saga started for match ${e.matchId}")

    // send commands to players
    // key/value map inside saga context, just keep the player ids
    SagaLifecycle.associateWith("bluePlayer1", e.teamBlue.player1.value)
    SagaLifecycle.associateWith("bluePlayer2", e.teamBlue.player2.value)
    SagaLifecycle.associateWith("redPlayer1", e.teamRed.player1.value)
    SagaLifecycle.associateWith("redPlayer2", e.teamRed.player2.value)

    // SagaLifecycle.associateWith("blue", e.teamBlue.toString())
    // SagaLifecycle.associateWith("red", e.teamBlue.toString())


    // FIXME: don't create any players from here. not deleted to discuss.
    // create players (-> Player), so players exists when win/loose is calculated
    val players = arrayOf(e.teamBlue.player1, e.teamBlue.player2, e.teamRed.player1, e.teamRed.player2)
    players.iterator().forEach { player ->
        commandGateway?.send<Any>(ParticipateInMatch(player = player, matchId = e.matchId))
        rankings.put(player, 0)
    }

  }

  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: PlayerParticipatedInMatch) {
    if (rankings.contains(e.player)) {
      logger.info("Player update ${e.player} has nee elo ${e.eloRanking}")
      rankings.put(e.player, e.eloRanking)
    } else {
      logger.info("Something bad happened ${e}")
    }
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: TeamWonMatch) {
    // make sure elo is provided
    if (validateElo(arrayOf(e.team.player1, e.team.player2, e.looser.player1, e.looser.player2))) {

      val teamResultElo = eloCalculationService!!.calculateTeamElo(
        Pair(rankings[e.team.player1]!!, rankings[e.team.player2]!!), // winner
        Pair(rankings[e.looser.player1]!!, rankings[e.looser.player2]!!) // looser
      )

      commandGateway?.send<Any>(UpdatePlayerRanking(player = e.team.player1, matchId = e.matchId, eloRanking = teamResultElo.first.first))
      commandGateway?.send<Any>(UpdatePlayerRanking(player = e.team.player2, matchId = e.matchId, eloRanking = teamResultElo.first.second))
      commandGateway?.send<Any>(UpdatePlayerRanking(player = e.looser.player1, matchId = e.matchId, eloRanking = teamResultElo.second.first))
      commandGateway?.send<Any>(UpdatePlayerRanking(player = e.looser.player1, matchId = e.matchId, eloRanking = teamResultElo.second.second))

    } else {
      // TODO exception
      logger.info("Something bad happened ${e}")
    }
  }


  fun validateElo(players: Array<UserName>) = players.all {  player -> rankings.containsKey(player) && rankings[player] != UNSET_ELO}
}

