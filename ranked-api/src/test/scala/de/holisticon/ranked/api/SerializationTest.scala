package de.holisticon.ranked.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import java.util.Date
import java.util.{List => JavaList}
import scala.collection.JavaConverters._
import de.holisticon.ranked.api.model._
import de.holisticon.ranked.api.model.TeamResult
import de.holisticon.ranked.api.model.MatchResult
import de.holisticon.ranked.api.model.MatchTeam
import de.holisticon.ranked.api.model.Round

/**
 * Test of match result
 * User: Simon
 */
class SerializationTest() {


  @Test
  def test() {
    val team1: MatchTeam = new MatchTeam(List(11L,12L).asJava)
    val team2: MatchTeam = new MatchTeam(List(37L,38L).asJava)
    val teams: List[MatchTeam]= List(team1, team2)

    val team1Result1: TeamResult = new TeamResult(team1, List(PlayerRole(team1.members.get(0), 1), PlayerRole(team1.members.get(1), 2)).asJava, 6)
    val team2Result1: TeamResult = new TeamResult(team2, List(PlayerRole(team2.members.get(0), 1), PlayerRole(team2.members.get(1), 2)).asJava, 1)
    val team1Result2: TeamResult = new TeamResult(team1, List(PlayerRole(team1.members.get(0), 2), PlayerRole(team1.members.get(1), 1)).asJava, 4)
    val team2Result2: TeamResult = new TeamResult(team2, List(PlayerRole(team2.members.get(0), 2), PlayerRole(team2.members.get(1), 1)).asJava, 6)
    val team1Result3: TeamResult = new TeamResult(team1, List(PlayerRole(team1.members.get(0), 1), PlayerRole(team1.members.get(1), 2)).asJava, 6)
    val team2Result3: TeamResult = new TeamResult(team2, List(PlayerRole(team2.members.get(0), 2), PlayerRole(team2.members.get(1), 1)).asJava, 5)


    val results: List[Round] = List(
      new Round(1, List(team1Result1, team2Result1).asJava),
      new Round(2, List(team1Result2, team2Result2).asJava),
      new Round(3, List(team1Result3, team2Result3).asJava)
    )


    val m = MatchResult(1L, 2L, "test", teams.asJava, results.asJava, new Date())
    val om = new ObjectMapper()
    Console.println(om.writeValueAsString(m))
  }
}
