@file:Suppress("UNUSED")

package de.holisticon.ranked.view.wall

import de.holisticon.ranked.model.AbstractMatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.MatchCreated
import de.holisticon.ranked.model.event.PlayerRankingChanged
import de.holisticon.ranked.model.event.TeamWonMatch
import de.holisticon.ranked.view.user.UserService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.Timestamp
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@Api(tags = ["News wall"])
@RestController
@RequestMapping(value = ["/view"])
class WallView(private val wall: WallService, private val users: UserService) {

  @ApiOperation(value = "Lists all users")
  @GetMapping("/user")
  fun users() = users.users.sortedBy { it.id }

  @ApiOperation(value = "Lists all matches")
  @GetMapping("/wall/matches")
  fun matches() = wall.matches

  @ApiOperation(value = "Lists all news")
  @GetMapping("/wall/news")
  fun news() = wall.news.sortBy { it.date }

}

@ProcessingGroup(WallService.NAME)
@Component
class WallService(private val userService: UserService) {

  companion object : KLogging() {
    const val NAME = "Wall"
  }


  val matches: MutableList<Match> = mutableListOf()
  val news: MutableList<NewsItem> = mutableListOf()

  @EventHandler
  fun on(e: MatchCreated, @Timestamp timestamp: Instant) {
    matches.add(Match(teamRed = e.teamRed, teamBlue = e.teamBlue, matchSets = e.matchSets, matchId = e.matchId, date = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC)))
  }

  @EventHandler
  fun on(e: TeamWonMatch, @Timestamp timestamp: Instant) {
    news.add(NewsItem(
      date = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC),
      message = "The team of ${format(e.team)} won against ${format(e.looser)}."
    ))
  }


  @EventHandler
  fun on(e: PlayerRankingChanged, @Timestamp timestamp: Instant) {
    news.add(NewsItem(
      date = LocalDateTime.ofInstant(timestamp, ZoneOffset.UTC),
      message = "${format(e.player)} has new ranking ${e.eloRanking}"
    ))
  }


  /**
   * Pretty prints player display name by username.
   */
  private fun format(username: UserName): String? {
    return userService.users.find { it.id == username.value }?.name
  }

  /**
   * Pretty prints team.
   */
  private fun format(team: Team): String {
    return "${format(team.player1)} and ${format(team.player2)}"
  }

}


data class Match(
  val matchId: String,
  val date: LocalDateTime,
  val teamRed: Team,
  val teamBlue: Team,
  val matchSets: List<AbstractMatchSet>
)

data class NewsItem(
  val date: LocalDateTime,
  val message: String
)


