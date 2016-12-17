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
import qube.qai.data.stores.TestStockEntityDataStore;
import qube.qai.data.stores.TestStockQuoteDataStore;
import qube.qai.matrix.TestMatrix;
import qube.qai.network.TestNetwork;
import qube.qai.network.TestNeuralNetwork;
import qube.qai.network.TestWikiNetwork;
import qube.qai.network.neural.trainer.TestNeuralNetworkTraining;
import qube.qai.network.semantic.TestSemanticNetworkBuilder;
import qube.qai.parsers.TestWikiIntegration;
import qube.qai.persistence.mapstores.*;
import qube.qai.procedure.TestWikiSearch;
import qube.qai.procedure.analysis.TestAnalysisProcedures;
import qube.qai.procedure.analysis.TestMarketNetworkBuilder;
import qube.qai.procedure.archive.TestDirectoryIndexer;
import qube.qai.procedure.archive.TestWikiArchiveIndexer;
import qube.qai.procedure.visitor.TestProcedureVisitors;
import qube.qai.procedure.wikiripper.TestWikiRipperProcedure;
import qube.qai.services.implementation.*;

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

    protected void log(String message) {
        if (debug) {
            //System.out.println(message);
            logger.info(message);
        }
    }

}
