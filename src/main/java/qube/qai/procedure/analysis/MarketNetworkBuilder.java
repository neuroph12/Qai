package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.procedure.ProcedureChain;

import java.util.Collection;

/**
 * Created by rainbird on 12/25/15.
 */
public class MarketNetworkBuilder extends ProcedureChain implements NetworkBuilder {

    public static String NAME = "Market Network Builder";

    public static String DESCRIPTION = "Creates and trains a neural-network out of the quotes of the stocks given";

    public static String INPUT_STOCK_ENTITY_COLLECTION = "input stock entity collection";

    public static String TRAINED_NEURAL_NETWORK = "trained neural network";

    public MarketNetworkBuilder() {
        super(NAME);
    }

    public Network buildNetwork(Selector source) {
        return null;
    }

    @Override
    public void execute() {

        Selector<Collection> stockEntities = arguments.getSelector(INPUT_STOCK_ENTITY_COLLECTION);

    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_STOCK_ENTITY_COLLECTION);
        arguments.putResultNames(NETWORK_METRICS,
                AVERAGE_TIME_SEQUENCE,
                CHANGE_POINTS,
                TRAINED_NEURAL_NETWORK);
    }
}
