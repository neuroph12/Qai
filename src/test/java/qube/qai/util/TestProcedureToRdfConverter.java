package qube.qai.util;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;
import qube.qai.services.implementation.UUIDService;

import java.util.logging.Logger;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureToRdfConverter extends TestCase {

    private Logger logger = Logger.getLogger("TestProcedureToRdfConverter");

    public void testRdfConverter() throws Exception {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);

        String uuid = UUIDService.uuidString();
        Model model = createDummyModel(uuid);
        assertNotNull("there has to be a model", model);


    }

    public void testModelConversion() throws Exception {

        Procedure procedure = createDummyProcedure();
        Model model = ProcedureToRdfConverter.createProcedureModel(procedure);

        Procedure backProc = ProcedureToRdfConverter.createProcedureFromModel("", model);

    }

    private Procedure createDummyProcedure() {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = null;

        return procedure;
    }

    private Model createDummyModel(String uuid) {
        Model model = null;

        return model;
    }

}
