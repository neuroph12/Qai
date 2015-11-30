package qube.qai.data;

import junit.framework.TestCase;
import qube.qai.data.selectors.DataSelector;
import qube.qai.matrix.Matrix;
import qube.qai.procedure.ProcedureChain;

/**
 * Created by rainbird on 11/29/15.
 */
public class TestProcedureChain extends TestCase {

    private boolean debug = true;

    public void testArguments() throws Exception {

        String inputMatrix = "input matrix";
        String variance = "variance";
        String mean = "mean";

        Arguments arguments = new Arguments();

        arguments.putNames(inputMatrix, variance, mean);

        DataSelector<Matrix> matrixSelector = new DataSelector<Matrix>(new Matrix());
        arguments.setArgument("input matrix", matrixSelector);
        arguments.setArgument("variance", new DataSelector<Double>(0.1243745));
        arguments.setArgument("mean", new DataSelector<Double>(0.43829));

        log(arguments.toString());

        assertTrue("all arguments must be satisfied", arguments.isSatisfied());
        assertTrue("input matrix is satisfied", arguments.hasValue(inputMatrix));
        assertTrue("variance has to be satisfied", arguments.hasValue(variance));
        assertTrue("mean has to be satisfied", arguments.hasValue(mean));
        assertTrue("invariance does not exist", !arguments.hasValue("invariance"));
    }

    public void testProcedureChain() throws Exception {

        ProcedureChain procedureChain = new ProcedureChain() {
            @Override
            public void run() {
                log("procedureChain.run()");
            }
        };
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
