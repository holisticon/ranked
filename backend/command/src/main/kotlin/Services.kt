package de.holisticon.ranked.command.service

import de.holisticon.ranked.command.api.CheckPlayer
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.extension.DefaultSmartLifecycle
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.properties.RankedProperties
import org.axonframework.commandhandling.CommandCallback
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Component
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign

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


@Component
class UserService {

  fun findUser(username: String): User? {

    // TODO replace with real User / Identity service
    return User(username, username.toUpperCase())
  }
}

data class User(
  val userName: String,
  val displayName: String
)

/**
 * Startup user creation.
 */
@Component
class UserInitializer(val commandGateway: CommandGateway) : DefaultSmartLifecycle() {

  fun initializeUsers() {

    arrayOf("kermit", "piggy", "gonzo", "fozzy", "beeker").forEach {
      val completableFuture = commandGateway.send(
        CheckPlayer(userName = UserName(it)),
        object : CommandCallback<CheckPlayer, Any> {
          override fun onSuccess(commandMessage: CommandMessage<out CheckPlayer>?, result: Any?) {
            // player exists
          }

          override fun onFailure(commandMessage: CommandMessage<out CheckPlayer>?, cause: Throwable?) {
            commandGateway.send<Any>(CreatePlayer(userName = UserName(it)))
          }
        }
      )
    }
  }

  override fun start() {
    initializeUsers()
    super.start()
  }

  override fun getPhase(): Int {
    return Int.MAX_VALUE - 20
  }
}

@Component
class EloCalculationService(val properties: RankedProperties) {

  /**
   * Calculates team elo rating.
   * @param winner a pair of elo rankings of the winner team before the match
   * @param looser a pair of elo rankings of the looser team before the match
   * @result a pair of new rankings for winner (first) and looser (second)
   */
  fun calculateTeamElo(winner: Pair<Int, Int>, looser: Pair<Int, Int>): Pair<Pair<Int, Int>, Pair<Int, Int>> {

    val winnerElo = (winner.first + winner.second)/2
    val looserElo = (looser.first + looser.second)/2

    val matchResult = calculateElo(Pair(winnerElo, looserElo))

    val winnerDelta = matchResult.first - winnerElo
    val looserDelta = looserElo - matchResult.second

    return Pair(
      Pair(winner.first + (winnerDelta / 2), winner.second + (winnerDelta / 2)),
      Pair(looser.first - (looserDelta / 2), looser.second - (looserDelta / 2))
    )
  }

  /**
   * Calculate new elo for a winner, looser elo pair.
   * @param current a pair with winner at first position and looser on second
   * @return new elo with winner on the first position and looser on the second.
   */
  fun calculateElo(current: Pair<Int, Int>): Pair<Int, Int> {
    var eloOffset = properties.elo.factor.times(1 - mean(current.first, current.second)).toInt()
    // don't allow to subtract more elo points as the looser has
    if (current.second <= eloOffset) {
      eloOffset = current.second
    }
    return Pair(current.first.plus(eloOffset), current.second.minus(eloOffset))
  }

  /**
   * Calculates the mean value.
   * @param ownElo own elo value
   * @param enemyElo enemy elo ranking
   * @return float probability of a win.
   */
  fun mean(ownElo: Int, enemyElo: Int): Float {
    var difference = enemyElo - ownElo
    if (difference.absoluteValue > properties.elo.maxDifference) {
      difference = properties.elo.maxDifference.times(difference.sign)
    }
    return 1 / (1 + Integer.valueOf(10).toFloat().pow(Integer.valueOf(difference).toFloat().div(properties.elo.maxDifference)))
  }

}

