package qube.qai.network.neural.function;

import qube.qai.network.neural.function.ActivationFunction;

/**
 * Created by rainbird on 11/23/15.
 */
public class LogitFunction implements ActivationFunction {

    /**
     * logit function is the inverse of sigmoid function and
     * defined as
     * logit(p) = log(p/(1-p))= log(p)-log(1-p)
     */
    public double invoke(double input) {

        return Math.log(input/(1-input));
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
