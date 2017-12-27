package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.elo.EloCalculationService
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.TeamWonMatch
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.EndSaga
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired


@Saga
class EloMatchSaga {

  companion object: KLogging() {
    val UNSET_ELO = -1
  }

  @Autowired
  @Transient
  lateinit var commandGateway:CommandGateway

  @Autowired
  @Transient
  lateinit var eloCalculationService: EloCalculationService

  private val rankings: MutableMap<UserName, Int> = mutableMapOf()

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: MatchCreated) {

    logger.trace("Elo saga started for match ${e.matchId}.")

    // send commands to players
    val players = arrayOf(e.teamBlue.player1, e.teamBlue.player2, e.teamRed.player1, e.teamRed.player2)
    players.iterator().forEach { player ->
        commandGateway?.send<Any>(ParticipateInMatch(userName = player, matchId = e.matchId))
        rankings.put(player, 0)
    }
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: PlayerParticipatedInMatch) {
    if (rankings.contains(e.player)) {
      logger.trace("Player ${e.player} participated in a match ${e.matchId} and had elo ranking ${e.eloRanking}.")
      rankings.put(e.player, e.eloRanking)
    } else {
      logger.error("Something bad happened ${e}")
    }
  }

  @EndSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: TeamWonMatch) {
    // make sure elo is provided
    if (validateElo(arrayOf(e.team.player1, e.team.player2, e.looser.player1, e.looser.player2))) {

      logger.trace("Elo saga calculated new rankings for the match ${e.matchId}.")
      val teamResultElo = eloCalculationService.calculateTeamElo(
        Pair(rankings[e.team.player1]!!, rankings[e.team.player2]!!), // winner
        Pair(rankings[e.looser.player1]!!, rankings[e.looser.player2]!!) // looser
      )

      commandGateway.send<Any>(UpdatePlayerRanking(userName = e.team.player1, matchId = e.matchId, eloRanking = teamResultElo.first.first))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = e.team.player2, matchId = e.matchId, eloRanking = teamResultElo.first.second))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = e.looser.player1, matchId = e.matchId, eloRanking = teamResultElo.second.first))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = e.looser.player2, matchId = e.matchId, eloRanking = teamResultElo.second.second))

    } else {
      // TODO exception
      logger.error("Something bad happened ${e}")
    }
  }


  fun validateElo(players: Array<UserName>) = players.all {  player -> rankings.containsKey(player) && rankings[player] != UNSET_ELO}
}

