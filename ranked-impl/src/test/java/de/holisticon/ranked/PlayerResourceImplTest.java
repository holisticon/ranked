package de.holisticon.ranked;

import de.holisticon.ranked.api.model.Player;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * @author Daniel
 */
@RunWith(Arquillian.class)
public class PlayerResourceImplTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(PlayerResourceImpl.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    PlayerResourceImpl resource;


    @Ignore("not yet implemented")
    @Test
    public void test() {
        resource.create(new Player("Name"));
    }
}
