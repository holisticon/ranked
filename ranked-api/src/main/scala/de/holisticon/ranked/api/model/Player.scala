package de.holisticon.ranked.api.model

import javax.persistence.{Entity, Basic}

/**
 * @author Daniel Wegener
 */
@Entity(name="player")
case class Player(name:String)
