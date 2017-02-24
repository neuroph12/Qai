package qube.qai.persistence.mapstores;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 12/21/15.
 */
public class TestHazelcastMaps extends QaiTestBase {

    @Inject
    private ProcedureSourceInterface procedureSource;

    @Inject
    private UUIDServiceInterface uuidService;

    @Inject
    @Named("Wikipedia_en")
    private SearchServiceInterface wikipediaSearch;

    @Inject
    @Named("Wiktionary_en")
    private SearchServiceInterface wiktionarySearch;

    @Inject //@Named("HAZELCAST_CLIENT")
    private HazelcastInstance hazelcastInstance;

    private static String STOCK_ENTITIES = "STOCK_ENTITIES";
    private static String PROCEDURES = "PROCEDURES";
    private static String WIKIPEDIA = "WIKIPEDIA_EN";
    private static String WIKTIONARY = "WIKTIONARY_EN";
    private static String WIKIPEDIA_RESOURCES = "WIKIPEDIA_RESOURCES";
    private static String WIKTIONARY_RESOURCES = "WIKTIONARY_RESOURCES";
    private static String[] wikipediaFilesToSearch = {"€2_commemorative_coin_Greece_2007_TOR.jpg",
            "3D_Hot_Rally_boxart.PNG",
            "1_on_1.jpg",
            "5Five-regular.jpg",
            "3_Group_badge.jpg",
            "3-D_Docking_Mission.png",
            "50_Foot_Wave_-_50_Foot_Wave_EP.jpg",
            "50_Years_of_Comparative_Wealth_E.P._cover.jpg",
            "8th_Weapons_Squadron.jpg",
            "Whutcha_Want.jpg"};

    private static String[] wiktionaryFilesToSearch = {"YR's_scripts.PNG",
            "Wiktionary-favicon-en-colored.png",
            "ipa-rendering-ff.png",
            "Wiktionary-favicon-en.png",
            "Writing_star.svg"};

//    @Override
//    protected void setUp() throws Exception {
//        hazelcastInstance = injector.getInstance(HazelcastInstance.class);
//    }

    public void testHazelcastWikipediaArticles() throws Exception {

        String[] someWikiArticles = {"mickey mouse", "mouse", "crow", "stock market"};

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKIPEDIA);
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

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKTIONARY);
        // again- we first do the search and demand the article
        for (String name : someWikiArticles) {
            Collection<SearchResult> results = wiktionarySearch.searchInputString(name, "title", 100);
            SearchResult result = results.iterator().next();
            WikiArticle wikiArticle = wikiArticles.get(result.getFilename());
            assertNotNull("there has to be an article", wikiArticle);
        }
    }

    public void testHazelcastStockEntities() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, StockEntity> stockEntities = hazelcastInstance.getMap(STOCK_ENTITIES);
        assertNotNull("there has to be a map", stockEntities);
        int number = 100;
        List<String> idList = new ArrayList<String>();
        for (int i = 0; i < number; i++) {
            String name = "e" + i + "x";
            StockEntity entity = createEntity(name);
            String id = entity.getUuid();
            if (!stockEntities.containsKey(id)) {
                stockEntities.put(id, entity);
            }
            idList.add(id);
        }

        for (String id : idList) {
            assertTrue("we just put this one 'ere", stockEntities.containsKey(id));
        }

        for (String id : stockEntities.keySet()) {
            StockEntity entity = stockEntities.get(id);
            assertNotNull(entity);
            if (!idList.contains(id)) {
                logger.info("not an entity of this test: " + entity.getName());
            }
        }

    }

    public void testHazelcastProcedures() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(PROCEDURES);
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

    public void testWikipediaResources() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, File> wikipediaResources = hazelcastInstance.getMap(WIKIPEDIA_RESOURCES);
        for (String filename : wikipediaFilesToSearch) {
            logger.info("now searching: " + filename);
            File found = wikipediaResources.get(filename);
            assertNotNull("has to be there something", found);
        }
    }

    public void testWiktionaryResources() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, File> wiktionaryResources = hazelcastInstance.getMap(WIKTIONARY_RESOURCES);
        for (String filename : wiktionaryFilesToSearch) {
            logger.info("now searching: " + filename);
            File found = wiktionaryResources.get(filename);
            assertNotNull("has to be there something", found);
        }
    }

    /**
     * creates a silly StockEntity
     *
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
