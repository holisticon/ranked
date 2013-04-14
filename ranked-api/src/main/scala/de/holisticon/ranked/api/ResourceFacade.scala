package de.holisticon.ranked.api

import javax.ws.rs._
import javax.ws.rs.core.MediaType
import de.holisticon.ranked.api.model._
import de.holisticon.ranked.api.model.Player
import de.holisticon.ranked.api.model.Discipline
import java.util.Date

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
  @Path("player/{id : \\d+}")
  def getPlayer(@PathParam("id") id:Long):Option[Player]

  @GET
  @Path("player/{name : \\D.+}")
  def getPlayerByName(@PathParam("name") name:String):Option[Player]

  @DELETE
  @Path("player/{id : \\d+}")
  def deletePlayer(@PathParam("id") id:Long):Unit

}

trait TournamentResource {

  @GET
  @Path("tournament")
  def getTournaments: List[Tournament]

  @POST
  @Path("tournament")
  def createTournament(@QueryParam("disciplineId") disciplineId: Long,
                       @QueryParam("name") name: String,
                       @QueryParam("start") start: Long,
                       @QueryParam("start") end: Long)

  @GET
  @Path("tournament/{id : \\d+}")
  def getTournament(@PathParam("id") id:Long):Option[Tournament]

  @GET
  @Path("tournament/{name : \\D.+}")
  def getTournamentByName(@PathParam("name") name:String):List[Tournament]




}


trait DisciplineResource {

  @GET
  @Path("discipline")
  def getDisciplines(): List[Discipline]

  @POST
  @Path("discipline")
  def createDiscipline(@QueryParam("name") name: String, @QueryParam("teamCount") teamCount:Int, @QueryParam("roundCount") roundCount:Int)

  @GET
  @Path("discipline/{id : \\d+}")
  def getDisciplineById(@PathParam("id") id:Long):Option[Discipline]

  @GET
  @Path("discipline/{name : \\D.+}")
  def getDisciplineByName(@PathParam("name") name:String):Option[Discipline]

}
