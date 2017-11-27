package de.holisticon.ranked.command.service

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class MatchService {

  @Value("\${ranked.score.match}")
  @Transient private lateinit var scoreToWinMatch: Integer


  @Value("\${ranked.score.set}")
  @Transient private lateinit var scoreToWinSet: Integer

  /**
   * Checks that the list of match sets consist of at least of scoreToWinMatch and at most of 2 * scoreToWinMatch - 1
   */
  fun validateMatch(matchSets: List<MatchSet>): Boolean {
    return matchSets.size >= scoreToWinMatch.toInt() && matchSets.size <= scoreToWinMatch.toInt().times(2).minus(1)
  }

  fun winsMatch(numberOfWins: Int) = numberOfWins == scoreToWinMatch.toInt()
  fun winsMatchSet(matchSet: MatchSet) = if (matchSet.goalsRed == scoreToWinSet.toInt()) Team.RED else Team.BLUE
}
