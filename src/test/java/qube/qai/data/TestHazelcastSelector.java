package qube.qai.data;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import junit.framework.TestCase;
import qube.main.QaiBaseTestCase;
import qube.main.QaiTestModule;

import javax.inject.Inject;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestHazelcastSelector extends QaiBaseTestCase {

    @Inject
    protected HazelcastInstance hazelcastInstance;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testHazelcastSelector() throws Exception {

        // @TODO add the actual test
        fail("Test not yet implemented!!!");
    }
}
