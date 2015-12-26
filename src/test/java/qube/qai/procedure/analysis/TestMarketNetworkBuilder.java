package qube.qai.procedure.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.Selector;
import qube.qai.data.selectors.DataSelector;
import qube.qai.data.stores.StockEntityDataStore;
import qube.qai.main.QaiTestBase;
import qube.qai.network.Network;
import qube.qai.persistence.StockEntity;

import java.util.*;

/**
 * Created by rainbird on 12/25/15.
 */
public class TestMarketNetworkBuilder extends QaiTestBase {

    private Logger logger = LoggerFactory.getLogger("TestMarketNetworkBuilder");
    private String SnP500Page = "List of S&P 500 companies.xml";
    /**
     * well this is actually pretty much it...
     * this is almost the moment of truth we have been waiting for...
     */
    public void testMarketBuilder() throws Exception {

        StockEntityDataStore dataStore = new StockEntityDataStore();
        injector.injectMembers(dataStore);

        Collection<StockEntity> entityList = dataStore.fetchEntitesOf(SnP500Page);

        // now we have the list of entities with which we want to build
        // the network for, we can simply pick, say 100 of them
        // and make a trial go with the thing
        Collection<StockEntity> workingSet = pickRandomFrom(10, entityList);
        Selector<Collection> selector = new DataSelector<Collection>();
        MarketNetworkBuilder networkBuilder = new MarketNetworkBuilder();
        injector.injectMembers(networkBuilder);
        Network network = networkBuilder.buildNetwork(selector);
        assertNotNull("duh", network);
        //assert...
    }

    private Collection<StockEntity> pickRandomFrom(int number, Collection<StockEntity> original) {
        Set<StockEntity> picked = new HashSet<StockEntity>();
        Random random = new Random();
        for (int i = 0; i <= number; i++) {
            int pick = random.nextInt(original.size());
            Iterator<StockEntity> it = original.iterator();
            for (int j = 0; j <= pick; j++) {
                StockEntity entity = it.next();
                if (j == pick) {
                    picked.add(entity);
                    break;
                }
            }
        }

        return picked;
    }
}
