package de.holisticon.ranked.command.service

import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import org.springframework.stereotype.Component

@Component
class MatchService(val scoreToWinMatch: Int, val scoreToWinSet: Int) {

  /**
   * Checks that the list of match sets consist of at least of scoreToWinMatch and at most of 2 * scoreToWinMatch - 1
   */
  fun validateMatch(matchSets: List<MatchSet>): Boolean {
    return matchSets.size >= scoreToWinMatch && matchSets.size <= scoreToWinMatch.times(2).minus(1)
  }

  fun winsMatch(numberOfWins: Int) = numberOfWins == scoreToWinMatch
  fun winsMatchSet(matchSet: MatchSet) = if (matchSet.goalsRed == scoreToWinSet) Team.RED else Team.BLUE
}


@Component
class UserService(val defaultElo: Int) {

  fun findUser(username: String) : User? {

    // TODO replace with real User / Identity service
    return User(username, username.toUpperCase())
  }

  fun getInitialElo() = defaultElo
}

data class User(
  val userName: String,
  val displayName: String
)
