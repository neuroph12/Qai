package qube.qai.network.neural.function;

import qube.qai.network.neural.function.ActivationFunction;

/**
 * Created by rainbird on 11/23/15.
 */
public class DiffSigmoidFunction implements ActivationFunction {

    /**
     * differentiated sigmoid function
     * defined as
     * dx = x*(1-x)
     */
    public double invoke(double input) {
        return input*(1.0-input);
    }

    public Number invoke(Number input) {
        return null;
    }

    public Object apply(Object o) {
        return null;
    }

    public Number apply(Number arg) {
        return null;
    }

    public double applyAsDouble(double arg) {
        return 0;
    }
}
