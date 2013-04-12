package de.holisticon.ranked.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Discipline

/**
 * Player resource.
 * @author Daniel Wegener (Holisticon AG)
 */

trait PlayerResource {

  @PUT
  @Path("player")
  def createPlayer(payload: Player)

  @GET
  @Path("player")
  def getPlayers: List[Player]

  @Path("player/{id}")
  def getPlayer(@PathParam("id") id:Long)

}


trait DisciplineResource {

  @Path("discipline")
  @GET
  def getDisciplines(): List[Discipline]

}
