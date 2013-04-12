package de.holisticon.ranked

import javax.ejb.{Stateless, LocalBean}
import de.holisticon.ranked.api.model.Discipline

/**
 * @author Daniel
 */
@Stateless
@LocalBean
class InitialEloProvider {


  def provideInitialElo(discipline:Discipline):Int = 1200

}
