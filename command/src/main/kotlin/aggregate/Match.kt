package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.api.WinMatch
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDateTime

@Aggregate
@Suppress("UNUSED")
class Match() {

  companion object {
    // Best of three.
    const val BEST_OF = 3
    const val SCORE_TO_WIN_MATCH = 2
  }

  @AggregateIdentifier
  private lateinit var matchId: String
  private lateinit var date: LocalDateTime



  @CommandHandler
  constructor(c: CreateMatch) : this() {
    apply(MatchCreated(
      matchId = c.matchId,
      teamBlue = c.teamBlue,
      teamRed = c.teamRed,
      date = c.date,
      matchSets = c.matchSets,
      tournamentId = c.tournamentId
    ))

    c.matchSets.forEach { m ->
      when (m.winner()) {
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
    apply(e)
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
    this.matchId = e.matchId
    this.date = e.date
  }

}
