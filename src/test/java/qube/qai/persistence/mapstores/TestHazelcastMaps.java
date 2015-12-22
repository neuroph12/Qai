package qube.qai.persistence.mapstores;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.main.QaiBaseTestCase;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;
import qube.qai.services.ProcedureSource;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.UUIDServiceInterface;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 12/21/15.
 */
public class TestHazelcastMaps extends QaiBaseTestCase {

    @Inject
    private ProcedureSource procedureSource;

    @Inject
    private UUIDServiceInterface uuidService;

    @Inject @Named("Wikipedia_en")
    private SearchServiceInterface wikipediaSearch;

    @Inject @Named("Wiktionary_en")
    private SearchServiceInterface wiktionarySearch;

    private static String STOCK_ENTITIES = "STOCK_ENTITIES";
    private static String PROCEDURES = "PROCEDURES";
    private static String WIKIPEDIA = "WIKIPEDIA_EN";
    private static String WIKTIONARY = "WIKTIONARY_EN";

    public void testHazelcastWikipediaArticles() throws Exception {

        String[] someWikiArticles = {"mickey mouse", "mouse", "crow", "stock market"};

        HazelcastInstance hazelcastInstance = injector.getInstance(HazelcastInstance.class);
        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String,WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKIPEDIA);
        // again- we first do the search and demand the article
        for (String name : someWikiArticles) {
            Collection<SearchResult> results = wikipediaSearch.searchInputString(name, "title", 100);
            SearchResult result = results.iterator().next();
            WikiArticle wikiArticle = wikiArticles.get(result.getFilename());
            assertNotNull("there has to be an article", wikiArticle);
        }
    }

    public void testHazelcastWiktionaryArticles() throws Exception {

        String[] someWikiArticles = {"mickey mouse", "mouse", "crow", "stock market"};

        HazelcastInstance hazelcastInstance = injector.getInstance(HazelcastInstance.class);
        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String,WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKTIONARY);
        // again- we first do the search and demand the article
        for (String name : someWikiArticles) {
            Collection<SearchResult> results = wiktionarySearch.searchInputString(name, "title", 100);
            SearchResult result = results.iterator().next();
            WikiArticle wikiArticle = wikiArticles.get(result.getFilename());
            assertNotNull("there has to be an article", wikiArticle);
        }
    }

    public void testHazelcastStockEntities() throws Exception {

        HazelcastInstance hazelcastInstance = injector.getInstance(HazelcastInstance.class);
        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String,StockEntity> stockEntities = hazelcastInstance.getMap(STOCK_ENTITIES);
        assertNotNull("there has to be a map", stockEntities);
        int number = 100;
        List<String> uuidList = new ArrayList<String>();
        for (int i = 0; i < number; i++) {
            String name = "entity(" + i + ")";
            StockEntity entity = TestMapStores.createEntity(name);
            String uuid = entity.getUuid();
            stockEntities.put(uuid, entity);
            uuidList.add(uuid);
        }

        for (String uuid : uuidList) {
            assertTrue("we just put this one 'ere", stockEntities.containsKey(uuid));
        }

        for (String uuid : stockEntities.keySet()) {
            StockEntity entity = stockEntities.get(uuid);
            assertNotNull(entity);
            if (!uuidList.contains(uuid)) {
                logger.info("not an entity of this test: " + entity.getName());
            }
        }

    }

    public void testHazelcastProcedures() throws Exception {

        HazelcastInstance hazelcastInstance = injector.getInstance(HazelcastInstance.class);
        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String,Procedure> procedureMap = hazelcastInstance.getMap(PROCEDURES);
        String[] procedureNames = procedureSource.getProcedureNames();

        // first get a hold of the procedures
        List<String> uuidList = new ArrayList<String>();
        for (String name : procedureNames) {
            Procedure procedure = procedureSource.getProcedureWithName(name);
            String uuid = procedure.getUuid();
            if (StringUtils.isBlank(uuid)) {
                uuid = uuidService.createUUIDString();
                procedure.setUuid(uuid);
            }
            procedureMap.put(uuid, procedure);
            uuidList.add(uuid);
        }

        for (String uuid : uuidList) {
            assertTrue("we just put this one 'ere", procedureMap.containsKey(uuid));
        }

        for (String uuid : uuidList) {
            Procedure procedure = procedureMap.get(uuid);
            assertNotNull("procedure should not be null", procedure);
        }
    }

}
