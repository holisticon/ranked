package de.holisticon.ranked;

import de.holisticon.ranked.api.model.*;
import de.holisticon.ranked.model.GenericDao;
import de.holisticon.ranked.model.GenericDaoForComposite;
import de.holisticon.ranked.model.PlayerDao;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import scala.Option;

import javax.ejb.EJB;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class PlayerResourceIT {

    @Deployment
    public static JavaArchive createDeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addPackages(
                        true,
                        "junit",
                        "org.junit",
                        "org.hamcrest",
                        Arquillian.class.getPackage().getName())
                        // classes from Impl
                .addPackages(true, GenericDao.class.getPackage())
                        // classes from Api
                .addPackages(true, PersistentEntity.class.getPackage())
                .addAsManifestResource("test-persistence.xml", "persistence.xml")
                .addAsManifestResource("arquillian-ds.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(archive.toString());
        System.out.println(archive.getContent());

        return archive;
    }

    @EJB
    PlayerDao resource;


    @Test
    public void testCreateAndFindPlayer() {
        resource.create(new Player("name", Collections.<Team>emptySet(), Collections.<Participation>emptySet(), Collections.<Ranking>emptySet()));
        final Option<Player> foundPlayer = resource.byName("name");
        MatcherAssert.assertThat(foundPlayer, notNullValue());
        MatcherAssert.assertThat(foundPlayer.get().getName(), equalTo("name"));
    }
}
