package qube.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import junit.framework.TestCase;
import javax.inject.Inject;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiBaseTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Injector injector = Guice.createInjector(new QaiTestModule());
        injector.injectMembers(this);
    }
}
