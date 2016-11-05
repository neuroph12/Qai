package qube.qai.network;

import qube.qai.data.SelectionOperator;

/**
 * Created by rainbird on 12/24/15.
 */
public interface NetworkBuilder {

    Network buildNetwork(SelectionOperator source);
}
