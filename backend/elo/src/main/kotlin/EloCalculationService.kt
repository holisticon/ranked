package de.holisticon.ranked.elo

import de.holisticon.ranked.properties.RankedProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign

@Component
class EloCalculationService(val factor:Int, val maxDifference:Int) {

  @Autowired
  constructor(properties: RankedProperties) : this(
    properties.elo.factor,
    properties.elo.maxDifference
  )

  /**
   * Calculates team elo rating.
   * @param winner a pair of elo rankings of the winner team before the match
   * @param looser a pair of elo rankings of the looser team before the match
   * @result a pair of new rankings for winner (first) and looser (second)
   */
  fun calculateTeamElo(winner: Pair<Int,Int>, looser: Pair<Int,Int>): Pair<Pair<Int,Int>, Pair<Int,Int>> {

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
    var eloOffset = factor.times(1 - mean(current.first, current.second)).toInt()
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
    if (difference.absoluteValue > maxDifference) {
      difference = maxDifference.times(difference.sign)
    }
    return 1 / (1 + Integer.valueOf(10).toFloat().pow(Integer.valueOf(difference).toFloat().div(maxDifference)))
  }

}
