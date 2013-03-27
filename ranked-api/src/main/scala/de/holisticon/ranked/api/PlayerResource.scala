package de.holisticon.ranked.api

import javax.ws.rs.{PUT, Path}
import model.Player

/**
 * @author Daniel Wegener (Holisticon AG)
 */
@Path("player")
trait PlayerResource {

  @PUT
  def create(payload:Player)


}
