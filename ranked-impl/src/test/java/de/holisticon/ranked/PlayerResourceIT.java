package de.holisticon.ranked;

import de.holisticon.ranked.api.model.PersistentEntity;
import de.holisticon.ranked.api.model.Player;
import de.holisticon.ranked.api.model.Tournament;
import de.holisticon.ranked.model.PlayerDao;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;

/**
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class PlayerResourceIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(PlayerDao.class, PersistentEntity.class, Player.class, Tournament.class)
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addAsResource("jbossas-ds.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @EJB
    PlayerDao resource;


    @Test
    public void test() {
        //resource.create(new Player("name"));

    }
}
