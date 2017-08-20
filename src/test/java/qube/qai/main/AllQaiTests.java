/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

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
import qube.qai.persistence.TestRdfSerialization;
import qube.qai.persistence.TestResourceData;
import qube.qai.persistence.mapstores.*;
import qube.qai.persistence.search.TestDatabaseSearchService;
import qube.qai.persistence.search.TestModelSearchServices;
import qube.qai.procedure.analysis.TestAnalysisProcedures;
import qube.qai.procedure.analysis.TestMarketNetworkBuilder;
import qube.qai.procedure.archive.TestArchiveProcedures;
import qube.qai.procedure.archive.TestDirectoryIndexer;
import qube.qai.procedure.archive.TestWikiArchiveIndexer;
import qube.qai.procedure.finance.TestFinanceProcedures;
import qube.qai.procedure.finance.TestStockEntityInitialization;
import qube.qai.procedure.finance.TestStockQuoteRetriever;
import qube.qai.procedure.nodes.TestProcedureInputsAndResults;
import qube.qai.procedure.utils.TestUtilProcedures;
import qube.qai.procedure.utils.TestWikiSearch;
import qube.qai.procedure.visitor.TestProcedureVisitors;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
import qube.qai.security.TestQaiSecurity;
import qube.qai.services.implementation.*;

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
     *
     * @return
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("All Tests");

        // data.analysis
        suite.addTestSuite(TestChangepointAdapter.class);

        // data.selectors
        suite.addTestSuite(TestHazelcastSelectors.class);

        // data.stores
//        suite.addTestSuite(TestStockQuoteDataStore.class);
//        suite.addTestSuite(TestStockEntityDataStore.class);

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

        // persistence
        suite.addTestSuite(TestResourceData.class);
        suite.addTestSuite(TestRdfSerialization.class);

        // persistence.mapstores
        suite.addTestSuite(TestMapStores.class);
        suite.addTestSuite(TestHazelcastMaps.class);
        suite.addTestSuite(TestIndexedDirectoryMapStore.class);
        suite.addTestSuite(TestDatabaseMapStores.class);
        suite.addTestSuite(TestModelMapStores.class);


        // persistence.search
        suite.addTestSuite(TestModelSearchServices.class);
        suite.addTestSuite(TestDatabaseSearchService.class);

        // procedure
        suite.addTestSuite(TestProcedureInputsAndResults.class);
        suite.addTestSuite(TestWikiArchiveIndexer.class);
        suite.addTestSuite(TestWikiRipperProcedure.class);

        // procedure.analysis
        suite.addTestSuite(TestAnalysisProcedures.class);
        suite.addTestSuite(TestMarketNetworkBuilder.class);

        // procedure.archive
        suite.addTestSuite(TestArchiveProcedures.class);
        suite.addTestSuite(TestDirectoryIndexer.class);
//        suite.addTestSuite(TestSparqlIndexer.class);

        // procedure.finance
        suite.addTestSuite(TestFinanceProcedures.class);
        suite.addTestSuite(TestStockEntityInitialization.class);
        suite.addTestSuite(TestStockQuoteRetriever.class);

        // procedure.visitor
        suite.addTestSuite(TestProcedureVisitors.class);

        // procedure.utils
        suite.addTestSuite(TestUtilProcedures.class);
        suite.addTestSuite(TestWikiSearch.class);

        // security
        suite.addTestSuite(TestQaiSecurity.class);

        // services.implementation
        suite.addTestSuite(TestUUIDGenerator.class);
        suite.addTestSuite(TestProcedureRunnerService.class);
        //suite.addTestSuite(TestDistributedSearchServices.class);
        //suite.addTestSuite(TestDistributedDataService.class);
        suite.addTestSuite(TestDistributedProcedureRunnerService.class);

        // these are more or less integration tests-
        suite.addTestSuite(TestHowFairAreMarketsDistributed.class);
        suite.addTestSuite(TestYouNMeNAllDistributed.class);
        suite.addTestSuite(TestTextTranslationDistributed.class);

        // util
        //suite.addTestSuite(TestProcedureToRdfConverter.class);

        return suite;
    }
}
