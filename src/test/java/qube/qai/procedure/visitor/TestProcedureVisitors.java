package qube.qai.procedure.visitor;

import junit.framework.TestCase;
import qube.qai.procedure.analysis.NeuralNetworkAnalysis;

/**
 * Created by rainbird on 12/1/15.
 */
public class TestProcedureVisitors extends TestCase {

    private boolean debug = true;

    public void testSimpleProcedureVisitor() throws Exception {

        // test the simple visitor on network analysis
        NeuralNetworkAnalysis networkAnalysis = (NeuralNetworkAnalysis) NeuralNetworkAnalysis.Factory.constructProcedure();

        SimpleProcedureVisitor visitor = new SimpleProcedureVisitor();
        String result = (String) networkAnalysis.childrenAccept(visitor, null);
        //assertNotNull("there has to be a result", result);
        log(visitor.toString());
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }

}
