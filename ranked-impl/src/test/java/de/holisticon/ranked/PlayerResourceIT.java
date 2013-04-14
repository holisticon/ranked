package de.holisticon.ranked;

import de.holisticon.ranked.api.model.*;
import de.holisticon.ranked.model.GenericDao;
import de.holisticon.ranked.model.PlayerDao;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import scala.Option;
import scala.collection.immutable.List;

import javax.ejb.EJB;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class PlayerResourceIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(PlayerDao.class, GenericDao.class, PersistentEntity.class, Player.class, Tournament.class, Match.class)
                .addPackage(Player.class.getPackage())
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addAsResource("jbossas-ds.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    PlayerDao resource;


    @Test
    public void testCreateAndFindPlayer() {
        resource.create(new Player("name", Collections.<Team>emptySet(), Collections.<Participation>emptySet(), Collections.<Ranking>emptySet()));
        final Option<Player> foundPlayer = resource.byName("name");
        assertThat(foundPlayer, notNullValue());
        assertThat(foundPlayer.get().getName(), equalTo("name"));
    }
}
