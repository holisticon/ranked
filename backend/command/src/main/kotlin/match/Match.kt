package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.CreateMatch
import de.holisticon.ranked.command.service.MatchService
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.TeamColor
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.model.event.TeamWonMatchSet
import mu.KLogging
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle.apply
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.beans.factory.annotation.Autowired

@Aggregate
@Suppress("UNUSED")
class Match() {

  companion object : KLogging()

  @AggregateIdentifier
  private lateinit var matchId: String

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
      matchSets = c.matchSets,
      startTime = c.startTime,
      tournamentId = c.tournamentId
    ))

    var teamWins: MutableMap<Team, Int> = mutableMapOf()

    var matchSetCompleted = false
    // (3) calculate which team won a set and fire TeamWonMatchSet event
    // **not** apply() but applyEvent() for further decomposition, each player won as well
    c.matchSets.forEach { m ->

      var setTeam: Team
      var setLooser: Team
      var setOffense: UserName

      when (matchService.winsMatchSet(m)) {
        TeamColor.BLUE -> {
          setTeam = c.teamBlue
          setLooser = c.teamRed
          setOffense = m.offenseBlue
        }
        TeamColor.RED -> {
          setTeam = c.teamRed
          setLooser = c.teamBlue
          setOffense = m.offenseRed
        }
      }

      apply(TeamWonMatchSet(
        team = setTeam,
        looser = setLooser,
        offense = setOffense,
        matchId = c.matchId
      ))

      // increase matchSet count for winner team
      val wins = teamWins.getOrDefault(setTeam, 0).inc()
      teamWins.put(setTeam, wins)

      // if this was the last set (one team won), fire event
      if (matchService.winsMatch(wins)) {
        matchSetCompleted = true
        apply(TeamWonMatch(
          matchId = c.matchId,
          team = setTeam,
          looser = setLooser
        ))
      }
    }

    if (!matchSetCompleted) {
      throw IllegalArgumentException("Match set has no winner.")
    }
  }

  /**
   * Remember the match id.
   */
  @EventSourcingHandler
  fun on(e: MatchCreated) {
    // (2) modify state of aggregate (must not be in command handler for aggregate restore)
    this.matchId = e.matchId
  }

}
