package de.holisticon.ranked.view.leaderboard

import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerRankingChanged
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*

class PlayerRankingByEloHandlerSpec {

  private val handler = PlayerRankingByEloHandler()

  @Before
  fun setUp() {
    // for each test, make sure no players are present
    assertThat(handler.get()).isEmpty()
  }



  @Test
  fun `a new player ranking is added`() {
    handler.on(PlayerRankingChanged(UserName("kermit"), 1000))

    val userByEloRank = handler.get()

    assertThat(userByEloRank).hasSize(1)
    assertThat(userByEloRank[0]).isEqualTo(PlayerElo("kermit", 1000))
  }

  @Test
  fun `a player ranking is updated`() {
    handler.on(PlayerRankingChanged(UserName("kermit"), 1000))
    handler.on(PlayerRankingChanged(UserName("kermit"), 2000))

    val userByEloRank = handler.get()

    assertThat(userByEloRank).hasSize(1)
    assertThat(userByEloRank[0]).isEqualTo(PlayerElo("kermit", 2000))
  }

  @Test
  fun `two players are sorted by rank`() {
    handler.on(PlayerRankingChanged(UserName("kermit"), 1000))
    handler.on(PlayerRankingChanged(UserName("piggy"), 2000))

    val userByEloRank = handler.get()

    assertThat(userByEloRank).hasSize(2)
    assertThat(userByEloRank[0]).isEqualTo(PlayerElo("piggy", 2000))
    assertThat(userByEloRank[1]).isEqualTo(PlayerElo("kermit", 1000))
  }
}


class PlayerEloSpec {

  @Test
  fun `other elo is greater`() {
    assertThat(PlayerElo("kermit", 1000).compareTo(PlayerElo("piggy", 2000))).isEqualTo(1)
  }

  @Test
  fun `other elo is smaller`() {
    assertThat(PlayerElo("kermit", 2000).compareTo(PlayerElo("piggy", 1000))).isEqualTo(-1)
  }

  @Test
  fun `sort list by comparable`() {
    val list = listOf(PlayerElo("kermit", 1000), PlayerElo("piggy", 2000)).sorted()

    assertThat(list.first().userName).isEqualTo(UserName("piggy"))
  }

  @Test
  fun `sort set by comparable`() {
    val tree = TreeSet<PlayerElo>()

    tree.add(PlayerElo("kermit", 1000))
    assertThat(tree.first().userName).isEqualTo(UserName("kermit"))

    tree.add(PlayerElo("piggy", 2000))
    assertThat(tree.first().userName).isEqualTo(UserName("piggy"))
  }
}
