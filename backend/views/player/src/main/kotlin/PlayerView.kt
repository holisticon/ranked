@file:Suppress("UNUSED")
package de.holisticon.ranked.view.player

import de.holisticon.ranked.model.Player
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.PlayerCreated
import de.holisticon.ranked.model.event.TeamCreated
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import mu.KLogging
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ProcessingGroup(PlayerViewService.NAME)
@RestController
@Api(tags = ["Players", "Teams"])
@RequestMapping(value = ["/view"])
class PlayerViewService {

  companion object : KLogging() {
    const val NAME = "Player"
  }

  val players: MutableSet<Player> = mutableSetOf()
  val teams: MutableSet<TeamFull> = mutableSetOf()
  // candidates of teams (missing user, denoted by the username, if both a re missing, the event is stored twice)
  val candidates: MutableMap<UserName, TeamCreated> = mutableMapOf()


  @ApiOperation(value = "Lists all players")
  @GetMapping("/player")
  fun findAllPlayers() = players.sortedBy { it.userName.value }

  @ApiOperation(value = "Lists all teams")
  @GetMapping("/teams")
  fun findAllTeams() = teams.sortedBy { it.name }


  @ApiOperation(value = "Get player by userName")
  @GetMapping("/player/{userName}")
  fun findPlayer(@PathVariable("userName") userName: String) = players.find { userName == it.userName.value }


  @EventHandler
  fun on(e: PlayerCreated) {
    players.add(
      Player(
        userName = e.userName,
        displayName = e.displayName,
        imageUrl = e.imageUrl,
        eloRanking = e.initialElo
      ))

    // try to add team, if it was a candidate
    val candidate = candidates[e.userName]
    if (candidate != null) {
      addTeam(candidate)
    }
  }


  @EventHandler
  fun on (e: TeamCreated) {
    // store the candidate
    candidates[e.team.player1] = e
    candidates[e.team.player2] = e

    // try to add team
    addTeam(e)
  }

  fun addTeam(e: TeamCreated) {
    val player1 = players.find { e.team.player1 == it.userName }
    val player2 = players.find { e.team.player2 == it.userName }

    if (player1 != null && player2 != null) {
      teams.add(TeamFull(
        name = e.name,
        id = e.id,
        player1 = player1,
        player2 = player2
      ))

      // remove
      candidates.remove(e.team.player1)
      candidates.remove(e.team.player2)
    }

  }
}

data class TeamFull(
  val name: String,
  val id: String,
  val player1 : Player,
  val player2 : Player
)
