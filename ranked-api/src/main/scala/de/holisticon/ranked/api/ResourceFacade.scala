package de.holisticon.ranked.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Discipline
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Discipline

/**
 * Player resource.
 * @author Daniel Wegener (Holisticon AG)
 */

trait PlayerResource {

  @POST
  @Path("player")
  def createPlayer(@QueryParam("name") name: String)

  @GET
  @Path("player")
  def getPlayers: List[Player]

  @GET
  @Path("player/{id}")
  def getPlayer(@PathParam("id") id:Long)

}


trait DisciplineResource {

  @GET
  @Path("discipline")
  def getDisciplines(): List[Discipline]


  @POST
  @Path("discipline")
  def createDiscipline(@QueryParam("name") name: String, @QueryParam("teamCount") teamCount:Int, @QueryParam("roundCount") roundCount:Int)

}
