package qube.qai.network.neural.function;

import qube.qai.network.neural.function.ActivationFunction;

/**
 * Created by rainbird on 11/23/15.
 */
public class SigmoidFunction implements ActivationFunction {

    /**
     * sigmpod function is used as the activation function and is defined as
     * s(p) = 1 / (1 + exp(-p))
     */
    public double invoke(double input) {
        return 1.0/(1 + Math.exp(-input));
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