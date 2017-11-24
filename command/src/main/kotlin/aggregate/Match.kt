package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.event.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
class Match() {

  companion object {
    // Best of three.
    const val SCORE_TO_WIN_MATCH = 2
  }

  @AggregateIdentifier
  private lateinit var matchId: String
  private var teamWins: MutableMap<Team, Int> = mutableMapOf()


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
          apply(TeamWonMatchSet(
            team = c.teamBlue,
            looser = c.teamRed,
            offense = m.offenseBlue,
            date = c.date
          ))
        }
        Team.RED -> {
          apply(TeamWonMatchSet(
            team = c.teamRed,
            looser = c.teamBlue,
            offense = m.offenseRed,
            date = c.date
          ))
        }
      }
    }
  }

  @EventSourcingHandler
  fun on(e: MatchCreated) {
    this.matchId = e.matchId
  }

  @EventSourcingHandler
  fun on(e: TeamWonMatchSet) {

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

    val wins = teamWins.getOrDefault(e.team, 0).inc()
    teamWins.put(e.team, wins)
    if (wins == SCORE_TO_WIN_MATCH) {
      apply(TeamWonMatch(
        team = e.team,
        looser = e.looser,
        date = e.date
      ))
    }
  }

  @EventSourcingHandler
  fun on(e: TeamWonMatch) {
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
}
