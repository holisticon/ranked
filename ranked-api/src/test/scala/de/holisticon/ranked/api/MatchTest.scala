package de.holisticon.ranked.api

import org.junit.Test
import de.holisticon.ranked.api.model.{Tournament, Match}
import java.util.{Collections, Date}
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * @author Daniel
 */
class MatchTest {

  @Test
  def test() = {
    val m = Match(new Date(),"A description",null,null, Collections.emptySet())
    val om = new ObjectMapper()
    Console.println(om.writeValueAsString(m))

  }

}
