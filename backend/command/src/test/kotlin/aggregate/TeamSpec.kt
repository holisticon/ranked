package de.holisticon.ranked.command.aggregate

import de.holisticon.ranked.command.api.*
import de.holisticon.ranked.model.Team
import de.holisticon.ranked.model.UserName
import de.holisticon.ranked.model.event.*
import de.holisticon.ranked.properties.createProperties
import org.axonframework.test.aggregate.AggregateTestFixture
import org.junit.Before
import org.junit.Test

class TeamSpec {

  private val fixture: AggregateTestFixture<TeamAggregate> = AggregateTestFixture(TeamAggregate::class.java)

  @Before
  fun init() {
  }

  @Test
  fun `when a create team command is received, a team is created`() {
    fixture
      .givenNoPriorActivity()
      .`when`(CreateTeam(id = "4711", name = "Muppet Team", team = Team(UserName("kermit"), UserName("piggy"))))
      .expectEvents(TeamCreated(id = "4711", name = "Muppet Team", team = Team(UserName("kermit"), UserName("piggy"))))
  }

  @Test
  fun `when a create team without players command is received, a team is created`() {
    fixture
      .givenNoPriorActivity()
      .`when`(CreateTeam(name = "Muppet Team", id = "4712"))
      .expectEvents(TeamCreated(id = "4712", name = "Muppet Team", team = Team(UserName("Muppet Team_player1"), UserName("Muppet Team_player2"))))
  }

  @Test
  fun `when a rename team command is received, a team is renamed`() {
    fixture
      .given(TeamCreated(id = "4711", name = "Muppet Team", team = Team(UserName("kermit"), UserName("piggy"))))
      .`when`(RenameTeam(id = "4711", newName = "Muppet Dream Team"))
      .expectEvents(TeamRenamed(id = "4711", name = "Muppet Dream Team"))
  }


}
