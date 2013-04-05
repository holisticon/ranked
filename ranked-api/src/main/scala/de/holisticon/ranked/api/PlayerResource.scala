package de.holisticon.ranked.api

import de.holisticon.ranked.api.model.Player
import javax.ws.rs._
import javax.ws.rs.core.MediaType
import de.holisticon.ranked.api.model.Player

/**
 * Player resource.
 * @author Daniel Wegener (Holisticon AG)
 */
@Path("player")
@Produces( Array ( MediaType.APPLICATION_JSON ))
@Consumes( Array ( MediaType.APPLICATION_JSON ))
trait PlayerResource {

  @PUT
  def create(payload: Player)

  @GET
  def get(): List[Player]

}
