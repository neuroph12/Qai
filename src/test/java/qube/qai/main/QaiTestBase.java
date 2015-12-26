package qube.qai.main;

import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TestTimeSequence;
import qube.qai.data.analysis.TestChangepointAdapter;
import qube.qai.data.selectors.TestHazelcastSelectors;
import qube.qai.data.stores.TestDataStore;
import qube.qai.data.stores.TestStockEntityDataStore;
import qube.qai.matrix.TestMatrix;
import qube.qai.network.TestNetwork;
import qube.qai.network.TestNeuralNetwork;
import qube.qai.network.TestWikiNetwork;
import qube.qai.network.neural.trainer.TestNeuralNetworkTraining;
import qube.qai.network.semantic.TestSemanticNetworkBuilder;
import qube.qai.parsers.TestWikiIntegration;
import qube.qai.persistence.mapstores.TestHazelcastMaps;
import qube.qai.persistence.mapstores.TestMapStores;
import qube.qai.persistence.mapstores.TestIndexedDirectoryMapStore;
import qube.qai.procedure.*;
import qube.qai.procedure.analysis.TestAnalysisProcedures;
import qube.qai.procedure.analysis.TestMarketNetworkBuilder;
import qube.qai.procedure.archive.TestDirectoryIndexer;
import qube.qai.procedure.visitor.TestProcedureVisitors;
import qube.qai.services.implementation.TestProcedureRunnerService;
import qube.qai.services.implementation.TestUUIDGenerator;

/**
 * Created by rainbird on 11/19/15.
 */
public class QaiTestBase extends TestCase {

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
        String[] tests = {QaiTestBase.class.getName()};
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
//        suite.addTestSuite(TestChangepointAdapter.class);
//        suite.addTestSuite(TestProcedureChain.class);
//
//        // data.selectors
//        suite.addTestSuite(TestHazelcastSelectors.class);
//
//        // services.implementation
//        suite.addTestSuite(TestUUIDGenerator.class);
//        suite.addTestSuite(TestProcedureRunnerService.class);
//
//        // data.stores
//        suite.addTestSuite(TestDataStore.class);
//        suite.addTestSuite(TestStockEntityDataStore.class);
//
//        // matrix
//        suite.addTestSuite(TestMatrix.class);
//        suite.addTestSuite(TestTimeSequence.class);
//
//        // network
//        suite.addTestSuite(TestNetwork.class);
//        suite.addTestSuite(TestNeuralNetwork.class);
//        suite.addTestSuite(TestNeuralNetworkTraining.class);
//        suite.addTestSuite(TestSemanticNetworkBuilder.class);
//        suite.addTestSuite(TestWikiNetwork.class);
//
//        // parsers
//        suite.addTestSuite(TestWikiIntegration.class);
//
//        // persistence.mapstores
//        suite.addTestSuite(TestMapStores.class);
//        suite.addTestSuite(TestHazelcastMaps.class);
//        suite.addTestSuite(TestIndexedDirectoryMapStore.class);
//
//        // procedure
//        suite.addTestSuite(TestWikiArchiveIndexer.class);
//        suite.addTestSuite(TestWikiRipperProcedure.class);
//        suite.addTestSuite(TestWikiSearch.class);
//
//        // procedure.analysis
//        suite.addTestSuite(TestAnalysisProcedures.class);
//        suite.addTestSuite(TestMarketNetworkBuilder.class);
//        suite.addTestSuite(TestMarketNetworkBuilder.class);
//
//        // procedure.archive
//        suite.addTestSuite(TestDirectoryIndexer.class);
//
//        // procedure.visitor
//        suite.addTestSuite(TestProcedureVisitors.class);
//
//        return suite;
//    }

}
