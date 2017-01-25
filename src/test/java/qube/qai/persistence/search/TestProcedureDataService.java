package qube.qai.persistence.search;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureFactory;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.services.implementation.SearchResult;
import qube.qai.util.ProcedureToRdfConverter;

import java.util.Collection;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureDataService extends TestCase {

    public void testProcedureDataService() throws Exception {

        ProcedureDataService dataService = new ProcedureDataService();

//        Collection<SearchResult> procedures = dataService.searchInputString("procedure", "*", 0);
//        assertNotNull("there has to be something", procedures);
//        assertTrue("the list may not be empty", !procedures.isEmpty());

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);
        Model model = ProcedureToRdfConverter.createProcedureModel(procedure);

        dataService.save(model);

        dataService.searchInputString("*", "uuid", 0);
    }
}
