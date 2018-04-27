package de.holisticon.ranked.command.api

import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import org.axonframework.commandhandling.TargetAggregateIdentifier
import java.util.*

/**
 * A command to create a team.
 */
data class CreateTeam(
  @TargetAggregateIdentifier
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val team: Team,
  val imageUrl: String
) {
  /**
   * Construct a team, based on team name only (creates anonymous players)
   * @param name of the team
   * @param id id visible to be able to test the eventing, defaults to UUID
   * @param imageUrl url where an icon for the team is located
   */
  constructor(name: String, imageUrl: String, id: String = UUID.randomUUID().toString()) : this(
    name = name,
    team = Team(
      player1 = UserName(name + "_player1"),
      player2 = UserName(name + "_player2")
    ),
    id = id,
    imageUrl = imageUrl
  )
}

/**
 * Command to rename a team.
 */
data class RenameTeam(
  @TargetAggregateIdentifier
  val id: String,
  val newName: String
)
