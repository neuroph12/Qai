package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.data.SelectionOperator;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.network.Network;
import qube.qai.network.NetworkBuilder;
import qube.qai.network.neural.NeuralNetwork;
import qube.qai.network.neural.trainer.BasicNetworkTrainer;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureDecorator;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by rainbird on 12/25/15.
 */
public class MarketNetworkBuilder extends ProcedureDecorator implements NetworkBuilder {

    public static String NAME = "Market Network Builder";

    public static String DESCRIPTION = "Creates and trains a neural-network out of the quotes of the stocks given";

    public static String INPUT_STOCK_ENTITY_COLLECTION = "input stock entity collection";

    public static String TRAINED_NEURAL_NETWORK = "trained neural network";

    public MarketNetworkBuilder(Procedure procedure) {
        super(NAME, procedure);
    }

    private BasicNetworkTrainer trainer;

    /**
     * calls execute method to train the network
     * and returns result of that
     * @param source
     * @return
     */
    public Network buildNetwork(SelectionOperator source) {

        arguments.setArgument(INPUT_STOCK_ENTITY_COLLECTION, source);
        execute();

        return (Network) getArguments().getResult(TRAINED_NEURAL_NETWORK);
    }

    @Inject
    private StockQuoteDataStore stockQuoteDataStore;

    @Override
    public void execute() {

        SelectionOperator<Collection> selectionOperator = arguments.getSelector(INPUT_STOCK_ENTITY_COLLECTION);
        Collection<StockEntity> entities = selectionOperator.getData();
        HashMap<String, Collection> trainingData = new HashMap<String, Collection>();
        NeuralNetwork network = new NeuralNetwork(entities.size());
        for (StockEntity entity : entities) {
            Network.Vertex vertex = new Network.Vertex(entity.getTickerSymbol());
            // while we are at it we collect the data here as well
            Collection<StockQuote> quotes = stockQuoteDataStore.retrieveQuotesFor(entity.getTickerSymbol());
            // if there are no available quotes, skip it and remove from list
            if (quotes != null || !quotes.isEmpty()) {
                network.addVertex(vertex);
                trainingData.put(entity.getTickerSymbol(), quotes);
            }
        }

        // well, here goes nothing
        trainer = new BasicNetworkTrainer(network);
        trainer.createTrainingSet(trainingData);
        trainer.trainNetwork();

        arguments.addResult(TRAINED_NEURAL_NETWORK, network);
    }

    /**
     * tickerSymbol is something like {{tradedIn|symbol}}
     * @param
     * @return
     */
//    private void correctTickerSymbol(StockEntity entity) {
//        String tradedIn = StringUtils.substringBetween(entity.getTickerSymbol(), "{{", "|");
//        String ticker = StringUtils.substringBetween(entity.getTickerSymbol(), "|", "}}");
//
//        entity.setTickerSymbol(ticker);
//        entity.setTradedIn(tradedIn);
//    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_STOCK_ENTITY_COLLECTION);
        arguments.putResultNames(NETWORK_METRICS,
                AVERAGE_TIME_SEQUENCE,
                CHANGE_POINTS,
                TRAINED_NEURAL_NETWORK);
    }

    public BasicNetworkTrainer getTrainer() {
        return trainer;
    }
}
