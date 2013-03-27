package de.holisticon.ranked.api

import de.holisticon.ranked.api.model.Player
import javax.ws.rs.{ GET, PUT, Path }

/**
 * @author Daniel Wegener (Holisticon AG)
 */
@Path("player")
trait PlayerResource {

  @PUT
  def create(payload: Player)

  @GET
  def get(): List[Player]

}
