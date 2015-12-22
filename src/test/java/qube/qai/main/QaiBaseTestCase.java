package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiBaseTestCase extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("QaiTest");

    protected boolean debug = true;

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

    /**
     * so that all of the tests are actually called
     * when this suite is in use
     * @return
     */
//    public static Test suite() {
//        TestSuite suite = new TestSuite("All tests");
//
//        // data.analysis
//        suite.addTestSuite(TestAnalysisProcedures.class);
//        suite.addTestSuite(TestChangepointAdapter.class);
//        suite.addTestSuite(TestProcedureChain.class);
//
//        // data.selectors
//        suite.addTestSuite(TestHazelcastSelectors.class);
//
//        // services.implementation
//        suite.addTestSuite(TestUUIDGenerator.class);
//        suite.addTestSuite(TestExecutionService.class);
//
//        // data.stores
//        suite.addTestSuite(TestDataStore.class);
//
//        // matrix
//        suite.addTestSuite(TestMatrix.class);
//        suite.addTestSuite(TestTimeSequence.class);
//
//        // network
//        suite.addTestSuite(TestNetwork.class);
//        suite.addTestSuite(TestNeuralNetwork.class);
//        suite.addTestSuite(TestNeuralNetworkTraining.class);
//        suite.addTestSuite(TestSemanticNetwork.class);
//        suite.addTestSuite(TestWikiNetwork.class);
//
//        // persistence.mapstores
//        suite.addTestSuite(TestMapStores.class);
//        suite.addTestSuite(TestHazelcastMaps.class);
//
//        // procedure
//        suite.addTestSuite(TestWikiArchiveIndexer.class);
//        suite.addTestSuite(TestWikiRipperProcedure.class);
//        suite.addTestSuite(TestWikiSearch.class);
//
//        // procedure.visitor
//        suite.addTestSuite(TestProcedureVisitors.class);
//
//        return suite;
//    }

}
