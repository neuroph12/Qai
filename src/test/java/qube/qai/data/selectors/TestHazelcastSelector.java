package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import qube.qai.main.QaiBaseTestCase;

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
