package de.holisticon.ranked.command.service

import de.holisticon.ranked.command.RankedProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EloServiceSpec {

  companion object {
      val MAX_MEAN = 0.9090909f
      val MIN_MEAN = 0.09090909f
      val PROBABILITY_MAX = 1f
  }


  private val testee: EloCalculationService = EloCalculationService(RankedProperties(
    scoreToWinSet = 6,
    scoreToWinMatch = 2,
    defaultElo = 1000,
    maxDifference = 400
  ))

  @Test
  fun ` understand float math first`() {
    assertThat(MAX_MEAN + MIN_MEAN).isEqualTo(PROBABILITY_MAX)
  }


  @Test
  fun `calculate equal mean`() {
    val mean = testee.mean(1000, 1000)
    assertThat(mean).isEqualTo(PROBABILITY_MAX.div(2))
  }

  @Test
  fun `calculate equal null mean`() {
    val mean = testee.mean(0, 0)
    assertThat(mean).isEqualTo(PROBABILITY_MAX.div(2))
  }

  @Test
  fun `calculate max mean`() {
    val mean = testee.mean(800, 400)
    assertThat(mean).isEqualTo(MAX_MEAN)
  }

  @Test
  fun `calculate max mean beyond threshold`() {
    val mean = testee.mean(1400, 400)
    assertThat(mean).isEqualTo(MAX_MEAN)
  }


  @Test
  fun `calculate min mean`() {
    val mean = testee.mean(400, 800)
    assertThat(mean).isEqualTo(MIN_MEAN)
  }

  @Test
  fun `calculate min mean below threshold`() {
    val mean = testee.mean(100, 800)
    assertThat(mean).isEqualTo(MIN_MEAN)
  }


  @Test
  fun `two means should result in probability of 1`() {
    val elo1 = 1431
    val elo2 = 1671
    val mean1 = testee.mean(elo1, elo2)
    val mean2 = testee.mean(elo2, elo1)
    assertThat(mean1 + mean2).isEqualTo(PROBABILITY_MAX)
  }

  @Test
  fun `calculate new elo`() {
    val elo = Pair(1431, 1671)
    val newElo = testee.calculateElo(elo)
    // winner should receive elo points
    assertThat(newElo.first).isGreaterThan(elo.first)
    // looser should loose elo points
    assertThat(newElo.second).isLessThan(elo.second)
    // elo is ranking, it only distributed elo points
    assertThat(newElo.first - elo.first).isEqualTo(elo.second - newElo.second)
  }

  @Test
  fun `calculate new elo by equal game`() {
    val elo = Pair(1431, 1431)
    val newElo = testee.calculateElo(elo)
    // winner should receive elo points
    assertThat(newElo.first).isGreaterThan(elo.first)
    // looser should loose elo points
    assertThat(newElo.second).isLessThan(elo.second)

    // by equal ranking, the probability to win is 50% (mean = 0.5f)
    // and eloFactor is 20, so the elo receipt/loss is 0,5 * 20 = 10 points
    val expected = (PROBABILITY_MAX.div(2) * testee.properties.eloFactor).toInt()

    assertThat(newElo.first - elo.first).isEqualTo(expected)
    assertThat(elo.second - newElo.second).isEqualTo(expected)
  }

  @Test
  fun `calculate zero touching elo`() {
    val elo = Pair(0, 7)
    val newElo = testee.calculateElo(elo)
    // winner should usually receive 10 elo points, but the looser only has 7
    assertThat(newElo.first).isEqualTo(elo.second)
    // looser should loose elo points
    assertThat(newElo.second).isEqualTo(0)
  }


  @Test
  fun `calculate team elo`() {

    // winner has team elo of (300 + 700) / 2 = 1000 / 2 = 500
    val winner = Pair(300, 700)
    // looser has team elo of (400 + 600) / 2 = 1000 / 2 = 500
    val looser = Pair(400, 600)

    // equal team elo => 10 point per team (got, lost)
    val result = testee.calculateTeamElo(winner, looser)
    // every winner won 5 points
    assertThat(result.first.first).isEqualTo(winner.first + 5)
    assertThat(result.first.second).isEqualTo(winner.second + 5)
    // every looser lost 5 points
    assertThat(result.second.first).isEqualTo(looser.first - 5)
    assertThat(result.second.second).isEqualTo(looser.second - 5)

    // elo is pure distribution, no elo points get into the system or from the system
    assertThat(winner.first + winner.second + looser.first + looser.second)
      .isEqualTo(result.first.first + result.first.second + result.second.first + result.second.second)
  }
}
