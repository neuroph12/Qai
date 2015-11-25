package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import qube.qai.network.TestSemanticNetwork;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiBaseTestCase extends TestCase {

    protected Injector injector;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        injector = Guice.createInjector(new QaiTestModule());
        injector.injectMembers(this);
    }

    public static void main(String[] params) {
        String[] tests = {QaiBaseTestCase.class.getName()};
        TestRunner.main(tests);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All tests");

        // persistence.mapstores
//        suite.addTestSuite(TestZipFileMapStore.class);
//
//        // procedure
//        suite.addTestSuite(TestWikiRipperProcedure.class);
//        suite.addTestSuite(TestWikiArchiveIndexer.class);
//        suite.addTestSuite(TestWikiSearch.class);

        // network
//        suite.addTestSuite(TestWikiNetwork.class);
        suite.addTestSuite(TestSemanticNetwork.class);
//        suite.addTestSuite(TestNeuralNetwork.class);

        return suite;
    }


}
