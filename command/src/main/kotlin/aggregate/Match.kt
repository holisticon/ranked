package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.*
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime

@Aggregate
@Suppress("UNUSED")
class Match() {

  companion object: KLogging()


  @AggregateIdentifier
  private lateinit var matchId: String
  private lateinit var date: LocalDateTime

  /**
   * (1) A Match aggregate is created, when a CreateMatch is received (via RestController).
   */
  @CommandHandler
  constructor(c: CreateMatch, @Autowired matchService: MatchService) : this() {
    // (2) a MatchCreated event is put to the bus
    // this is handled by all EventSourcingHandlers (first) and all external EventHandlers (second)
    // this just indicates that a Match happened and contains no logic for win/loose
    apply(MatchCreated(
      matchId = c.matchId,
      teamBlue = c.teamBlue,
      teamRed = c.teamRed,
      date = c.date,
      matchSets = c.matchSets,
      tournamentId = c.tournamentId
    ))

    // (3) calculate which team won a set and fire TeamWonMatchSet event
    // **not** apply() but applyEvent() for further decomposition, each player won as well
    c.matchSets.forEach { m ->
      when (matchService.winsMatchSet(m)) {
        Team.BLUE -> {
          this.applyEvent(TeamWonMatchSet(
            team = c.teamBlue,
            looser = c.teamRed,
            offense = m.offenseBlue,
            date = c.date,
            matchId = c.matchId
          ))
        }
        Team.RED -> {
          this.applyEvent(TeamWonMatchSet(
            team = c.teamRed,
            looser = c.teamBlue,
            offense = m.offenseRed,
            date = c.date,
            matchId = c.matchId
          ))
        }
      }
    }
  }

  @CommandHandler
  fun on(c: WinMatch) {
    // fire TeamWonMatch
    applyEvent(TeamWonMatch(
      matchId = this.matchId,
      team = c.winner,
      looser = c.looser,
      date = this.date
    ))
  }

  /**
   * Apply the event and all subsequent events.
   */
  fun applyEvent(e: TeamWonMatchSet) {
    logger.info{"${e}"}
    // (3) event: TeamWonMatchSet
    // -> MatchWinnerSaga#handle(e: TeamWonMatchSet)
    apply(e)
    // (3) each player won a set
    apply(PlayerWonMatchSet(
      player = e.team.player1,
      teammate = e.team.player2,
      position = if (e.offense == e.team.player1) PlayerPosition.OFFENSE else PlayerPosition.DEFENSE,
      date = e.date
    ))
    apply(PlayerWonMatchSet(
      player = e.team.player2,
      teammate = e.team.player1,
      position = if (e.offense == e.team.player2) PlayerPosition.OFFENSE else PlayerPosition.DEFENSE,
      date = e.date
    ))
  }

  /**
   * Apply the event and all subsequent events.
   */
  fun applyEvent(e: TeamWonMatch) {
    // the team won -> view, ranking calculation (ELO)
    apply(e)
    // each player won -> view
    apply(PlayerWonMatch(
      player = e.team.player1,
      teammate = e.team.player2,
      date = e.date
    ))
    apply(PlayerWonMatch(
      player = e.team.player2,
      teammate = e.team.player1,
      date = e.date
    ))
  }

  /**
   * Remember the match id.
   */
  @EventSourcingHandler
  fun on(e: MatchCreated) {
    // (2) modify state of aggregate (must not be in command handler for aggregate restore)
    this.matchId = e.matchId
    this.date = e.date
  }

}
