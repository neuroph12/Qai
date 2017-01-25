package qube.qai.util;

import junit.framework.TestCase;
import org.apache.jena.rdf.model.Model;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureToRdfConverter extends TestCase {

    public void testRdfCOnverter() throws Exception {

        SelectionProcedure selection = new SelectionProcedure();
        Procedure procedure = NeuralNetworkAnalysis.Factory.constructProcedure(selection);

        Model model = ProcedureToRdfConverter.createProcedureModel(procedure);
        assertNotNull("there has to be a model", model);
    }
}
