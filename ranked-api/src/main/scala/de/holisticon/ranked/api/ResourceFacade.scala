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
@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/player")
trait PlayerResource {

  @POST
  def createPlayer(@QueryParam("name") name: String)

  @GET
  def getPlayers: List[Player]

  @GET
  @Path("/{id : \\d+}")
  def getPlayer(@PathParam("id") id:Long):Option[Player]

  @GET
  @Path("/{name : \\D.+}")
  def getPlayerByName(@PathParam("name") name:String):Option[Player]

  @DELETE
  @Path("/{id : \\d+}")
  def deletePlayer(@PathParam("id") id:Long):Unit

}

@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/tournament")
trait TournamentResource {

  @GET
  def getTournaments: List[Tournament]

  @POST
  def createTournament(@QueryParam("disciplineId") disciplineId: Long,
                       @QueryParam("name") name: String,
                       @QueryParam("start") start: Long,
                       @QueryParam("end") end: Long)

  @GET
  @Path("/{id : \\d+}")
  def getTournament(@PathParam("id") id:Long):Option[Tournament]

  @GET
  @Path("/{name : \\D.+}")
  def getTournamentByName(@PathParam("name") name:String):List[Tournament]

}

@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/discipline")
trait DisciplineResource {

  @GET
  def getDisciplines(): List[Discipline]

  @POST
  def createDiscipline(@QueryParam("name") name: String, @QueryParam("teamCount") teamCount:Int, @QueryParam("roundCount") roundCount:Int)

  @GET
  @Path("/{id : \\d+}")
  def getDisciplineById(@PathParam("id") id:Long):Option[Discipline]

  @GET
  @Path("/{name : \\D.+}")
  def getDisciplineByName(@PathParam("name") name:String):Option[Discipline]

}

@Produces(Array(MediaType.APPLICATION_JSON))
@Path("/match")
trait MatchResource {

  @POST
  def createMatch(matchResult: MatchResult)
}
