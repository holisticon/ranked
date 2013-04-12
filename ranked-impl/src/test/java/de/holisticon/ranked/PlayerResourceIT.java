package de.holisticon.ranked;

import de.holisticon.ranked.api.model.PersistentEntity;
import de.holisticon.ranked.api.model.Player;
import de.holisticon.ranked.api.model.Tournament;
import de.holisticon.ranked.model.GenericDao;
import de.holisticon.ranked.model.PlayerDao;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import scala.collection.immutable.List;

import javax.ejb.EJB;

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
                .addClasses(PlayerDao.class, GenericDao.class, PersistentEntity.class, Player.class, Tournament.class)
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addAsResource("jbossas-ds.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    PlayerDao resource;


    @Test
    public void testCreateAndFindPlayer() {
        resource.create(new Player("name", null, null));
        final List<Player> foundPlayer = resource.byName("name");
        assertThat(foundPlayer, notNullValue());
        assertThat(foundPlayer.apply(0).getName(), equalTo("name"));
    }
}
