package de.holisticon.ranked.command.service

import de.holisticon.ranked.command.RankedProperties
import de.holisticon.ranked.command.api.CreatePlayer
import de.holisticon.ranked.model.MatchSet
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component

@Component
class MatchService(val properties: RankedProperties) {

  /**
   * Checks that the list of match sets consist of at least of scoreToWinMatch and at most of 2 * scoreToWinMatch - 1
   */
  fun validateMatch(matchSets: List<MatchSet>): Boolean =
    matchSets.size >= properties.scoreToWinMatch && matchSets.size <= properties.scoreToWinMatch.times(2).minus(1)

  fun winsMatch(numberOfWins: Int) = numberOfWins == properties.scoreToWinMatch
  fun winsMatchSet(matchSet: MatchSet): String = if (matchSet.goalsRed == properties.scoreToWinSet) Team.RED else Team.BLUE
}


@Component
class UserService {

  fun findUser(username: String) : User? {

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
class UserInitializer(val userService: UserService, val commandGateway: CommandGateway) : SmartLifecycle {

  companion object {
    val USERS = arrayOf("kermit", "piggy", "gonzo", "fozzy")
  }

  fun initializeUsers() {
    USERS.forEach { user -> commandGateway.send<Any>(CreatePlayer(userName = UserName(user))) }
  }

  var running: Boolean = false

  override fun start() {
    initializeUsers()
    this.running = true
  }

  override fun isAutoStartup(): Boolean { return true }

  override fun stop(callback: Runnable?) {
    callback?.run()
    this.running = false
  }

  override fun stop() { this.running = false }

  override fun getPhase(): Int {
    return Int.MAX_VALUE - 20
  }

  override fun isRunning(): Boolean { return running }
}
