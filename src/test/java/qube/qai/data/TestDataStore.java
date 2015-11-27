package qube.qai.data;

import junit.framework.TestCase;
/**
 * Created by rainbird on 11/25/15.
 */
public class TestDataStore extends TestCase {

    private boolean debug = true;

    public void testDataStoreWithHazelcastInstance() throws Exception {

        // @TODO the test is not implemented
        log("tests are not yet implemented");
        fail("tests are not yet implemented");
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
