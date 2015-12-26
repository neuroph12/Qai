package qube.qai.procedure.analysis;

import org.apache.commons.lang.StringUtils;
import qube.qai.data.Arguments;
import qube.qai.data.Selector;
import qube.qai.data.TimeSequence;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.trainer.BasicNetworkTrainer;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.ProcedureChain;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;

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

    @Inject
    private StockQuoteDataStore stockQuoteDataStore;

    @Override
    public void execute() {

        Selector<Collection> selector = arguments.getSelector(INPUT_STOCK_ENTITY_COLLECTION);
        Collection<StockEntity> entities = selector.getData();
        HashMap<String, Collection> entityData = new HashMap<String, Collection>();
        NeuralNetwork network = new NeuralNetwork(entities.size());
        for (StockEntity entity : entities) {
            correctTickerSymbol(entity);
            Network.Vertex vertex = new Network.Vertex(entity.getTickerSymbol());
            // while we are at it we collect the data here as well
            Collection<StockQuote> quotes = stockQuoteDataStore.retrieveQuotesFor(entity.getTickerSymbol());
            // if there are no available quotes, skip it and remove from list
            if (quotes != null || !quotes.isEmpty()) {
                network.addVertex(vertex);
                entityData.put(entity.getTickerSymbol(), quotes);
            }
        }

        // well, here goes nothing
        BasicNetworkTrainer trainer = new BasicNetworkTrainer(network);
        // etc...

    }

    /**
     * tickerSymbol is something like {{tradedIn|symbol}}
     * @param entity
     * @return
     */
    private void correctTickerSymbol(StockEntity entity) {
        String tradedIn = StringUtils.substringBetween(entity.getTickerSymbol(), "{{", "|");
        String ticker = StringUtils.substringBetween(entity.getTickerSymbol(), "|", "}}");

        entity.setTickerSymbol(ticker);
        entity.setTradedIn(tradedIn);
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
