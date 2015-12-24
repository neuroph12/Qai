package qube.qai.network;

import qube.qai.data.Selector;
import qube.qai.matrix.Matrix;

/**
 * Created by rainbird on 12/24/15.
 */
public interface NetworkBuilder {

    Network buildNetwork(Selector source);
}
