package de.holisticon.ranked;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;

import de.holisticon.ranked.api.PlayerResource;
import de.holisticon.ranked.api.model.Player;

/**
 * @author Daniel
 */
public class PlayerResourceImpl implements PlayerResource {

	@Override
	@PUT
	public void create(Player payload) {
		
	}

	@Override
	@GET
	public scala.collection.immutable.List<Player> get() {
		return null;
	}


}
