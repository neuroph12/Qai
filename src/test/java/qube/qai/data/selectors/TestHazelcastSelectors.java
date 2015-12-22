package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.data.Selector;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.persistence.mapstores.TestMapStores;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSourceInterface;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 11/19/15.
 */
public class TestHazelcastSelectors extends QaiBaseTestCase {

    @Inject
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureSourceInterface procedureSource;

    @Inject
    private UUIDServiceInterface uuidService;

    @Inject @Named("Wikipedia_en")
    private SearchServiceInterface wikipediaSearch;

    @Inject @Named("Wiktionary_en")
    private SearchServiceInterface wiktionarySearch;

    private String STOCK_SOURCE = "STOCK_ENTITIES";
    private String PROCEDURE_SOURCE = "PROCEDURES";
    private String WIKIPEDIA_SOURCE = "WIKIPEDIA_EN";
    private String WIKTIONARY_SOURCE = "WIKTIONARY_EN";

    /**
     * test for selector of stock-entities
     * @throws Exception
     */
    public void testHazelcastStockEntities() throws Exception {

        IMap<String,StockEntity> stockEntities = hazelcastInstance.getMap(STOCK_SOURCE);

        int number = 100;
        Collection<Selector> selectors = new ArrayList<Selector>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = TestMapStores.createEntity(name);
            String uuid = entity.getUuid();
            stockEntities.put(uuid, entity);
            Selector<StockEntity> selector = new HazelcastSelector<StockEntity>(STOCK_SOURCE, uuid);
            selectors.add(selector);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (Selector selector : selectors) {
            injector.injectMembers(selector);
            StockEntity entity = (StockEntity) selector.getData();
            assertNotNull("there has to be a stock entity", entity);
        }
    }

    /**
     * test for procedure selectors
     * @throws Exception
     */
    public void testHazelcastProcedures() throws Exception {

        IMap<String,Procedure> procedures = hazelcastInstance.getMap(PROCEDURE_SOURCE);

        List<Selector> selectors = new ArrayList<Selector>();
        String[] procedureNames = procedureSource.getProcedureNames();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            assertNotNull("procedure should not be null", procedure);
            String uuid = procedure.getUuid();
            if (StringUtils.isBlank(uuid)) {
                uuid = uuidService.createUUIDString();
                procedure.setUuid(uuid);
            }
            Selector<Procedure> selector = new HazelcastSelector<Procedure>(PROCEDURE_SOURCE, uuid);
            procedures.put(uuid, procedure);
            selectors.add(selector);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (Selector selector : selectors) {
            injector.injectMembers(selector);
            Procedure procedure = (Procedure) selector.getData();
            assertNotNull("there has to be a procedure", procedure);
        }
    }

    /**
     * this is mainly for testing how the selectors for wiki-articles work
     * there shouldn't really a problem with this
     * @throws Exception
     */
    public void testHazelcastWikipediaArticles() throws Exception {

        IMap<String,WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKIPEDIA_SOURCE);

        List<Selector> selectors = new ArrayList<Selector>();
        Collection<SearchResult> results = wikipediaSearch.searchInputString("mouse", "title", 100);
        for (SearchResult result : results) {
            Selector<WikiArticle> selector = new HazelcastSelector<WikiArticle>(WIKIPEDIA_SOURCE, result.getFilename());
            selectors.add(selector);
        }

        // now collect the results
        for (Selector<WikiArticle> selector : selectors) {
            injector.injectMembers(selector);
            WikiArticle article = selector.getData();
            assertNotNull("article may not be null", article);
        }
    }

    /**
     * this is mainly for testing how the selectors for wiki-articles work
     * there shouldn't really a problem with this
     * @throws Exception
     */
    public void testHazelcastWiktionaryArticles() throws Exception {

        IMap<String,WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKTIONARY_SOURCE);

        List<Selector> selectors = new ArrayList<Selector>();
        Collection<SearchResult> results = wiktionarySearch.searchInputString("mouse", "title", 100);
        for (SearchResult result : results) {
            Selector<WikiArticle> selector = new HazelcastSelector<WikiArticle>(WIKTIONARY_SOURCE, result.getFilename());
            selectors.add(selector);
        }

        // now collect the results
        for (Selector<WikiArticle> selector : selectors) {
            injector.injectMembers(selector);
            WikiArticle article = selector.getData();
            assertNotNull("article may not be null", article);
        }
    }

}
