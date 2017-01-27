package qube.qai.main;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.TestTimeSequence;
import qube.qai.data.analysis.TestChangepointAdapter;
import qube.qai.data.selectors.TestHazelcastSelectors;
import qube.qai.data.stores.TestStockEntityDataStore;
import qube.qai.data.stores.TestStockQuoteDataStore;
import qube.qai.matrix.TestMatrix;
import qube.qai.network.TestNetwork;
import qube.qai.network.TestNeuralNetwork;
import qube.qai.network.TestWikiNetwork;
import qube.qai.network.neural.trainer.TestNeuralNetworkTraining;
import qube.qai.network.semantic.TestSemanticNetworkBuilder;
import qube.qai.parsers.TestAntimirovParser;
import qube.qai.parsers.TestWikiIntegration;
import qube.qai.parsers.antimirov.nodes.TestNodeVisitors;
import qube.qai.parsers.maths.TestMathParser;
import qube.qai.persistence.TestModelStore;
import qube.qai.persistence.mapstores.TestDatabaseMapStores;
import qube.qai.persistence.mapstores.TestHazelcastMaps;
import qube.qai.persistence.mapstores.TestIndexedDirectoryMapStore;
import qube.qai.persistence.mapstores.TestMapStores;
import qube.qai.persistence.search.TestProcedureDataService;
import qube.qai.persistence.search.TestRDFTripleSearchService;
import qube.qai.persistence.search.TestStockQuoteSearchService;
import qube.qai.procedure.TestWikiSearch;
import qube.qai.procedure.analysis.TestAnalysisProcedures;
import qube.qai.procedure.analysis.TestMarketNetworkBuilder;
import qube.qai.procedure.archive.TestDirectoryIndexer;
import qube.qai.procedure.archive.TestWikiArchiveIndexer;
import qube.qai.procedure.finance.TestStockEntityInitialization;
import qube.qai.procedure.visitor.TestProcedureVisitors;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
import qube.qai.services.implementation.*;
import qube.qai.util.TestProcedureToRdfConverter;

/**
 * Created by rainbird on 5/26/16.
 */
public class AllQaiTests extends TestCase {

    protected Logger logger = LoggerFactory.getLogger("AllQaiTests");

    public static void main(String[] params) {
        String[] tests = {AllQaiTests.class.getName()};
        TestRunner.main(tests);
    }

    /**
     * so that all of the tests are actually called
     * when this suite is in use
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Tests");

        // data.analysis
        suite.addTestSuite(TestChangepointAdapter.class);

        // data.selectors
        suite.addTestSuite(TestHazelcastSelectors.class);

        // data.stores
        suite.addTestSuite(TestStockQuoteDataStore.class);
        suite.addTestSuite(TestStockEntityDataStore.class);

        // matrix
        suite.addTestSuite(TestMatrix.class);
        suite.addTestSuite(TestTimeSequence.class);

        // network
        suite.addTestSuite(TestNetwork.class);
        suite.addTestSuite(TestNeuralNetwork.class);
        suite.addTestSuite(TestNeuralNetworkTraining.class);
        suite.addTestSuite(TestSemanticNetworkBuilder.class);
        suite.addTestSuite(TestWikiNetwork.class);

        // parsers
        suite.addTestSuite(TestWikiIntegration.class);
        suite.addTestSuite(TestAntimirovParser.class);
        suite.addTestSuite(TestMathParser.class);

        // parsers.antimirov.nodes
        suite.addTestSuite(TestNodeVisitors.class);

        // persistence.mapstores
        suite.addTestSuite(TestMapStores.class);
        suite.addTestSuite(TestHazelcastMaps.class);
        suite.addTestSuite(TestIndexedDirectoryMapStore.class);
        suite.addTestSuite(TestDatabaseMapStores.class);
        suite.addTestSuite(TestModelStore.class);

        // persistence.search
        suite.addTestSuite(TestStockQuoteSearchService.class);
        suite.addTestSuite(TestRDFTripleSearchService.class);
        suite.addTestSuite(TestProcedureDataService.class);

        // procedure
        suite.addTestSuite(TestWikiArchiveIndexer.class);
        suite.addTestSuite(TestWikiRipperProcedure.class);
        suite.addTestSuite(TestWikiSearch.class);
        suite.addTestSuite(TestStockEntityInitialization.class);

        // procedure.analysis
        suite.addTestSuite(TestAnalysisProcedures.class);
        suite.addTestSuite(TestMarketNetworkBuilder.class);

        // procedure.archive
        suite.addTestSuite(TestDirectoryIndexer.class);

        // procedure.visitor
        suite.addTestSuite(TestProcedureVisitors.class);

        // services.implementation
        suite.addTestSuite(TestUUIDGenerator.class);
        suite.addTestSuite(TestProcedureRunnerService.class);
        suite.addTestSuite(TestDistributedSearchServices.class);
        suite.addTestSuite(TestDistributedProcedureRunnerService.class);
        suite.addTestSuite(TestYouNMeNAllDistributed.class);
        suite.addTestSuite(TestHowFairAreMarketsDistributed.class);
        suite.addTestSuite(TestTextTranslationDistributed.class);

        // util
        suite.addTestSuite(TestProcedureToRdfConverter.class);

        return suite;
    }
}
