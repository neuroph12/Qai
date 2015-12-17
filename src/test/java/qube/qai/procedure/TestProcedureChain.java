package qube.qai.procedure;

import junit.framework.TestCase;
import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.matrix.Matrix;

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

        ProcedureChain sumChain = createTestProcedure();
        sumChain.getArguments().setArgument("x", new DataSelector<Double>(Double.valueOf(2.0)));
        sumChain.getArguments().setArgument("y", new DataSelector<Double>(Double.valueOf(3.0)));


        assertTrue("sum-chain procedure's arguments must be satisfied", sumChain.getArguments().isSatisfied());
        sumChain.run();

        double sum = (Double) sumChain.getArguments().getSelector("sum").getData();
        assertTrue("sum has to be five", sum == 5.0);
    }

    private ProcedureChain createTestProcedure() {

        ProcedureChain procedureChain = new ProcedureChain() {
            @Override
            public void execute() {
                log("procedureChain.run()");
                double x = (Double) arguments.getSelector("x").getData();
                double y = (Double) arguments.getSelector("y").getData();
                log("input x: " + x + " and input y: " + y);
                double sum = x + y;
                Selector<Double> sumSelector = new DataSelector<Double>(sum);
                log("return value selector with sum: " + sum);
                arguments.putNames("sum");
                arguments.setArgument("sum", sumSelector);
            }

            @Override
            public void buildArguments() {
                arguments = new Arguments();
                arguments.putNames("x", "y");
            }
        };

        procedureChain.buildArguments();

        return procedureChain;
    }

    private void log(String message) {
        if (debug) {
            System.out.println(message);
        }
    }
}
