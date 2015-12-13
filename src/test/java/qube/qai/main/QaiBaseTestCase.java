package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import qube.qai.matrix.TestTimeSeries;
import qube.qai.network.*;
import qube.qai.procedure.TestAnalysisProcedures;
import qube.qai.data.analysis.TestChangepointAdapter;
import qube.qai.data.analysis.TestProcedureChain;
import qube.qai.data.selectors.TestHazelcastSelector;
import qube.qai.data.stores.TestDataStore;
import qube.qai.matrix.TestMatrix;
import qube.qai.persistence.mapstores.TestMapStore;
import qube.qai.procedure.TestNeuralNetworkAnalysis;
import qube.qai.procedure.TestWikiArchiveIndexer;
import qube.qai.procedure.TestWikiRipperProcedure;
import qube.qai.procedure.TestWikiSearch;
import qube.qai.procedure.visitor.TestProcedureVisitors;
import qube.qai.services.implementation.TestUUIDGenerator;

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

    /**
     * so that all of the tests are actually called
     * when this suite is in use
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All tests");

        // data.analysis
        suite.addTestSuite(TestAnalysisProcedures.class);
        suite.addTestSuite(TestChangepointAdapter.class);
        suite.addTestSuite(TestProcedureChain.class);

        // data.selectors
        suite.addTestSuite(TestHazelcastSelector.class);

        // services.implementation
        suite.addTestSuite(TestUUIDGenerator.class);

        // data.stores
        suite.addTestSuite(TestDataStore.class);

        // matrix
        suite.addTestSuite(TestMatrix.class);
        suite.addTestSuite(TestTimeSeries.class);

        // network
        suite.addTestSuite(TestNetwork.class);
        suite.addTestSuite(TestNeuralNetwork.class);
        suite.addTestSuite(TestNeuralNetworkTraining.class);
        suite.addTestSuite(TestSemanticNetwork.class);
        suite.addTestSuite(TestWikiNetwork.class);

        // persistence.mapstores
        suite.addTestSuite(TestMapStore.class);

        // procedure
        suite.addTestSuite(TestNeuralNetworkAnalysis.class);
        // @TODO test-data for these test is missing-
        // in this form they are useless and should not be executed!?!
//        suite.addTestSuite(TestWikiArchiveIndexer.class);
//        suite.addTestSuite(TestWikiRipperProcedure.class);
        suite.addTestSuite(TestWikiSearch.class);

        // procedure.visitor
        suite.addTestSuite(TestProcedureVisitors.class);

        return suite;
    }


}
