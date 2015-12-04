package qube.qai.network.neural.function;

import org.ojalgo.function.UnaryFunction;

/**
 * Created by rainbird on 11/23/15.
 */
public interface ActivationFunction extends UnaryFunction {

    double invoke(double input);

    Number invoke(Number input);
}
