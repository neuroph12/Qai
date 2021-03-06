/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
import qube.qai.data.TimeSequenceTest;
import qube.qai.data.analysis.TestChangepointAdapter;
import qube.qai.matrix.MatrixTest;
import qube.qai.network.NetworkTest;
import qube.qai.network.finance.FinanceNetworkBuilderTest;
import qube.qai.network.formal.FormalNetworkTest;
import qube.qai.network.neural.NeuralNetworkTest;
import qube.qai.network.neural.trainer.NeuralNetworkTrainingTest;
import qube.qai.network.semantic.SemanticNetworkBuilderTest;
import qube.qai.network.syntax.SyntaxNetworkBuilderTest;
import qube.qai.network.wiki.WikiNetworkBuilderTest;
import qube.qai.parsers.antimirov.nodes.NodeVisitorsTest;
import qube.qai.parsers.grammar.GrammarParserTest;
import qube.qai.parsers.maths.MathParserTest;
import qube.qai.persistence.RdfSerializationTest;
import qube.qai.persistence.ResourceDataTest;
import qube.qai.persistence.mapstores.*;
import qube.qai.persistence.search.DatabaseSearchServiceTest;
import qube.qai.persistence.search.ModelSearchServicesTest;
import qube.qai.procedure.analysis.AnalysisProceduresTest;
import qube.qai.procedure.archive.ArchiveProceduresTest;
import qube.qai.procedure.archive.DirectoryIndexerTest;
import qube.qai.procedure.archive.WikiArchiveIndexerTest;
import qube.qai.procedure.finance.FinanceProceduresTest;
import qube.qai.procedure.finance.StockEntityInitializationTest;
import qube.qai.procedure.nodes.ProcedureInputsAndResultsTest;
import qube.qai.procedure.utils.UtilProceduresTest;
import qube.qai.procedure.utils.WikiSearchTest;
import qube.qai.procedure.visitor.ProcedureVisitorsTest;
import qube.qai.procedure.wikiripper.WikiRipperProcedureTest;
import qube.qai.security.ProcedureManagerTest;
import qube.qai.security.QaiSecurityTest;
import qube.qai.services.implementation.ProcedureRunnerServiceTest;
import qube.qai.services.implementation.UUIDGeneratorTest;
import qube.qai.services.implementation.WikiSearchServiceTest;
import qube.qai.services.sims.HowFairAreMarketsTest;
import qube.qai.services.sims.MDSimsOfBiochemicalReactionsTest;
import qube.qai.services.sims.MachineTranslationTest;
import qube.qai.services.sims.YouNMeNEveryoneWeKnowTest;

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
        //suite.addTestSuite(TestHazelcastSelectors.class);

        // data.stores
//        suite.addTestSuite(TestStockQuoteDataStore.class);
//        suite.addTestSuite(TestStockEntityDataStore.class);

        // matrix
        suite.addTestSuite(MatrixTest.class);
        suite.addTestSuite(TimeSequenceTest.class);

        // network
        suite.addTestSuite(NetworkTest.class);
        suite.addTestSuite(NeuralNetworkTest.class);
        suite.addTestSuite(NeuralNetworkTrainingTest.class);
        suite.addTestSuite(SemanticNetworkBuilderTest.class);
        suite.addTestSuite(SyntaxNetworkBuilderTest.class);
        suite.addTestSuite(WikiNetworkBuilderTest.class);
        suite.addTestSuite(FormalNetworkTest.class);

        // parsers
        //suite.addTestSuite(WikiIntegrationTest.class);
        //suite.addTestSuite(TestAntimirovParser.class);
        suite.addTestSuite(GrammarParserTest.class);
        suite.addTestSuite(MathParserTest.class);

        // parsers.antimirov.nodes
        suite.addTestSuite(NodeVisitorsTest.class);

        // persistence
        suite.addTestSuite(ResourceDataTest.class);
        suite.addTestSuite(RdfSerializationTest.class);

        // persistence.mapstores
        suite.addTestSuite(MapStoreTest.class);
        suite.addTestSuite(HazelcastMapsTest.class);
        suite.addTestSuite(IndexedDirectoryMapStoreTest.class);
        suite.addTestSuite(DatabaseMapStoresTest.class);
        suite.addTestSuite(ModelMapStoresTest.class);


        // persistence.wiki
        suite.addTestSuite(ModelSearchServicesTest.class);
        suite.addTestSuite(DatabaseSearchServiceTest.class);
        suite.addTestSuite(WikiSearchServiceTest.class);

        // procedure
        //suite.addTestSuite(ProcedureLibraryTest.class);
        suite.addTestSuite(ProcedureManagerTest.class);
        suite.addTestSuite(ProcedureInputsAndResultsTest.class);
        suite.addTestSuite(WikiArchiveIndexerTest.class);
        suite.addTestSuite(WikiRipperProcedureTest.class);

        // procedure.analysis
        suite.addTestSuite(AnalysisProceduresTest.class);
        suite.addTestSuite(FinanceNetworkBuilderTest.class);

        // procedure.archive
        suite.addTestSuite(ArchiveProceduresTest.class);
        suite.addTestSuite(DirectoryIndexerTest.class);
//        suite.addTestSuite(TestSparqlIndexer.class);

        // procedure.finance
        suite.addTestSuite(FinanceProceduresTest.class);
        suite.addTestSuite(StockEntityInitializationTest.class);


        // procedure.visitor
        suite.addTestSuite(ProcedureVisitorsTest.class);

        // procedure.utils
        suite.addTestSuite(UtilProceduresTest.class);
        suite.addTestSuite(WikiSearchTest.class);

        // security
        suite.addTestSuite(QaiSecurityTest.class);

        // services.implementation
        suite.addTestSuite(UUIDGeneratorTest.class);
        suite.addTestSuite(ProcedureRunnerServiceTest.class);
        //suite.addTestSuite(TestDistributedSearchServices.class);
        //suite.addTestSuite(TestDistributedDataService.class);
        //suite.addTestSuite(DistributedProcedureRunnerServiceTest.class);

        // these are more or less integration tests-
        // or rather the homework assignments expressed in code form
        suite.addTestSuite(HowFairAreMarketsTest.class);
        suite.addTestSuite(YouNMeNEveryoneWeKnowTest.class);
        suite.addTestSuite(MachineTranslationTest.class);
        suite.addTestSuite(MDSimsOfBiochemicalReactionsTest.class);

        // util
        //suite.addTestSuite(TestProcedureToRdfConverter.class);

        return suite;
    }
}
