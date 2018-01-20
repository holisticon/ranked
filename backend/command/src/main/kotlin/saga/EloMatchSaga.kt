@file:Suppress("UNUSED")

package de.holisticon.ranked.command.saga

import de.holisticon.ranked.command.api.ParticipateInMatch
import de.holisticon.ranked.command.api.UpdatePlayerRanking
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerParticipatedInMatch
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.service.elo.EloCalculationService
import mu.KLogging
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.saga.SagaEventHandler
import org.axonframework.eventhandling.saga.SagaLifecycle.end
import org.axonframework.eventhandling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired


@Saga
class EloMatchSaga {

  companion object : KLogging() {
    const val UNSET_ELO = -1
  }

  @Autowired
  @Transient
  lateinit var commandGateway: CommandGateway

  @Autowired
  @Transient
  lateinit var eloCalculationService: EloCalculationService

  private val rankings: MutableMap<UserName, Int> = mutableMapOf()
  private lateinit var winner: Team
  private lateinit var looser: Team
  private lateinit var matchId: String

  @StartSaga
  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: MatchCreated) {

    this.matchId = e.matchId
    logger.trace("Elo saga started for match $matchId.")

    // send commands to players
    arrayOf(e.teamBlue.player1, e.teamBlue.player2, e.teamRed.player1, e.teamRed.player2)
      .forEach {
        // initialize elo
        rankings.put(it, UNSET_ELO)
        // send participation request
        commandGateway.send<Any>(ParticipateInMatch(userName = it, matchId = matchId))
      }
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: PlayerParticipatedInMatch) {
    if (rankings.contains(e.player) && e.matchId == matchId) {
      logger.trace("Player ${e.player} participated in a match ${e.matchId} and had elo ranking ${e.eloRanking}.")
      rankings.put(e.player, e.eloRanking)

      calculateElo()
    } else {
      logger.error("Something bad happened on receipt of $e. The player is unknown and unexpected for elo saga $matchId.")
    }
  }

  @SagaEventHandler(associationProperty = "matchId")
  fun on(e: TeamWonMatch) {

    this.winner = e.team
    this.looser = e.looser

    calculateElo()
  }


  fun calculateElo() {
    // make sure elo is provided and winner and looser are determined
    if (validateElo()) {
      logger.trace("Elo saga calculated new rankings for the match $matchId.")
      val teamResultElo = eloCalculationService.calculateTeamElo(
        Pair(rankings[winner.player1]!!, rankings[winner.player2]!!), // winner
        Pair(rankings[looser.player1]!!, rankings[looser.player2]!!) // looser
      )

      commandGateway.send<Any>(UpdatePlayerRanking(userName = winner.player1, matchId = matchId, eloRanking = teamResultElo.first.first))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = winner.player2, matchId = matchId, eloRanking = teamResultElo.first.second))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = looser.player1, matchId = matchId, eloRanking = teamResultElo.second.first))
      commandGateway.send<Any>(UpdatePlayerRanking(userName = looser.player2, matchId = matchId, eloRanking = teamResultElo.second.second))

      // end saga
      end()
    }
  }

  fun validateElo(): Boolean {
    return ::winner.isInitialized
      && ::looser.isInitialized
      && arrayOf(winner.player1, winner.player2, looser.player1, looser.player2).all { player ->
        rankings.containsKey(player) && rankings[player] != UNSET_ELO
      }
  }

}

