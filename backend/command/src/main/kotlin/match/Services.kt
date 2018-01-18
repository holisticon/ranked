package de.holisticon.ranked.command.service

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.properties.RankedProperties
import org.springframework.stereotype.Component

@Component
class MatchService(val properties: RankedProperties) {

  /**
   * Checks that the list of match sets consist of at least of scoreToWinMatch and at most of 2 * scoreToWinMatch - 1
   */
  fun validateMatch(matchSets: List<MatchSet>): Boolean =
    matchSets.size >= properties.setsToWinMatch && matchSets.size <= properties.setsToWinMatch.times(2).minus(1)

  fun winsMatch(numberOfWins: Int) = numberOfWins == properties.setsToWinMatch
  fun winsMatchSet(matchSet: MatchSet): String = if (matchSet.goalsRed == properties.scoreToWinSet) Team.RED else Team.BLUE

}
