package qube.qai.data.selectors;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.data.SelectionOperator;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
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
public class TestHazelcastSelectors extends QaiTestBase {

    @Inject @Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    @Inject
    private ProcedureSourceInterface procedureSource;

    @Inject
    private UUIDServiceInterface uuidService;

    @Inject @Named("Wikipedia_en")
    private SearchServiceInterface wikipediaSearch;

    @Inject @Named("Wiktionary_en")
    private SearchServiceInterface wiktionarySearch;

    private String STOCK_ENTITIES = "STOCK_ENTITIES";
    private String PROCEDURE_SOURCE = "PROCEDURES";
    private String WIKIPEDIA_SOURCE = "WIKIPEDIA_EN";
    private String WIKTIONARY_SOURCE = "WIKTIONARY_EN";

    /**
     * test for selector of stock-entities
     * @throws Exception
     */
    public void testHazelcastStockEntities() throws Exception {

        IMap<String,StockEntity> stockEntities = hazelcastInstance.getMap(STOCK_ENTITIES);

        int number = 100;
        Collection<SelectionOperator> selectionOperators = new ArrayList<SelectionOperator>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = createEntity(name);
            String entityId = entity.getUuid();
            if (!stockEntities.containsKey(entityId)) {
                stockEntities.put(entityId, entity);
            }
            SelectionOperator<StockEntity> selectionOperator = new HazelcastSelectionOperator<StockEntity>(STOCK_ENTITIES, entityId);
            selectionOperators.add(selectionOperator);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (SelectionOperator selectionOperator : selectionOperators) {
            injector.injectMembers(selectionOperator);
            StockEntity entity = (StockEntity) selectionOperator.getData();
            assertNotNull("there has to be a stock entity", entity);
        }
    }

    /**
     * test for procedure selectors
     * @throws Exception
     */
    public void testHazelcastProcedures() throws Exception {

        IMap<String,Procedure> procedures = hazelcastInstance.getMap(PROCEDURE_SOURCE);

        List<SelectionOperator> selectionOperators = new ArrayList<SelectionOperator>();
        String[] procedureNames = procedureSource.getProcedureNames();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            assertNotNull("procedure should not be null", procedure);
            String uuid = procedure.getUuid();
            if (StringUtils.isBlank(uuid)) {
                uuid = uuidService.createUUIDString();
                procedure.setUuid(uuid);
            }
            SelectionOperator<Procedure> selectionOperator = new HazelcastSelectionOperator<Procedure>(PROCEDURE_SOURCE, uuid);
            procedures.put(uuid, procedure);
            selectionOperators.add(selectionOperator);
        }

        // now after putting everything in hazelcast we should be able to read them as well
        for (SelectionOperator selectionOperator : selectionOperators) {
            injector.injectMembers(selectionOperator);
            Procedure procedure = (Procedure) selectionOperator.getData();
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

        List<SelectionOperator> selectionOperators = new ArrayList<SelectionOperator>();
        Collection<SearchResult> results = wikipediaSearch.searchInputString("mouse", "title", 100);
        for (SearchResult result : results) {
            SelectionOperator<WikiArticle> selectionOperator = new HazelcastSelectionOperator<WikiArticle>(WIKIPEDIA_SOURCE, result.getFilename());
            selectionOperators.add(selectionOperator);
        }

        // now collect the results
        for (SelectionOperator<WikiArticle> selectionOperator : selectionOperators) {
            injector.injectMembers(selectionOperator);
            WikiArticle article = selectionOperator.getData();
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

        List<SelectionOperator> selectionOperators = new ArrayList<SelectionOperator>();
        Collection<SearchResult> results = wiktionarySearch.searchInputString("mouse", "title", 100);
        for (SearchResult result : results) {
            SelectionOperator<WikiArticle> selectionOperator = new HazelcastSelectionOperator<WikiArticle>(WIKTIONARY_SOURCE, result.getFilename());
            selectionOperators.add(selectionOperator);
        }

        // now collect the results
        for (SelectionOperator<WikiArticle> selectionOperator : selectionOperators) {
            injector.injectMembers(selectionOperator);
            WikiArticle article = selectionOperator.getData();
            assertNotNull("article may not be null", article);
        }
    }

    /**
     * creates a silly StockEntity
     * @param name
     * @return
     */
    public static StockEntity createEntity(String name) {
        StockEntity entity = new StockEntity();
        entity.setName(name);
        entity.setAddress("address of " + name);
        entity.setGicsSector("gicsSector of " + name);
        entity.setGicsSubIndustry("gicsSubIndustry of " + name);
        entity.setSecurity("security of " + name);
        entity.setTradedIn("vsex");
        entity.setTickerSymbol(name);
        return entity;
    }
}
