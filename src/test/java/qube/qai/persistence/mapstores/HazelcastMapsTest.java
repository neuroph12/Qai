/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.persistence.mapstores;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import qube.qai.main.QaiTestBase;
import qube.qai.persistence.ResourceData;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureLibrary;
import qube.qai.procedure.ProcedureTemplate;
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
 * Created by rainbird on 12/21/15.
 */
public class HazelcastMapsTest extends QaiTestBase {

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

    @Inject
    private HazelcastInstance hazelcastInstance;

    private static String STOCK_ENTITIES = "StockEntities";
    private static String PROCEDURES = "Procedures";
    private static String WIKIPEDIA = "Wikipedia_en";
    private static String WIKTIONARY = "Wiktionary_en";
    private static String WIKIPEDIA_RESOURCES = "WikiResources_en";
    private static String WIKTIONARY_RESOURCES = "WiktionaryResources_en";
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

    public void testHazelcastWikipediaArticles() throws Exception {

        String[] someWikiArticles = {"mickey mouse", "mouse", "crow", "stock market"};

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, WikiArticle> wikiArticles = hazelcastInstance.getMap(WIKIPEDIA);
        // again- we first do the search and demand the article
        for (String name : someWikiArticles) {
            Collection<SearchResult> results = wikipediaSearch.searchInputString(name, "title", 100);
            SearchResult result = results.iterator().next();
            WikiArticle wikiArticle = wikiArticles.get(result.getUuid());
            assertNotNull("there has to be an article for '" + result.getUuid() + "'", wikiArticle);
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
            WikiArticle wikiArticle = wikiArticles.get(result.getUuid());
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

    /**
     * @throws Exception
     * @deprecated this method is no longer needed as the persistence of procedures
     * has been changed and now is done in a different way.
     */
    public void testHazelcastProcedures() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, Procedure> procedureMap = hazelcastInstance.getMap(PROCEDURES);
        //Collection<Class> procedureClasses = Procedure.knownSubClasses();

        // first get a hold of the procedures
        List<String> uuidList = new ArrayList<String>();
        for (ProcedureTemplate template : ProcedureLibrary.getTemplateMap().values()) {
            Procedure procedure = template.createProcedure();
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

    /**
     * @throws Exception
     */
    public void testWikipediaResources() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, ResourceData> wikipediaResources = hazelcastInstance.getMap(WIKIPEDIA_RESOURCES);
        for (String filename : wikipediaFilesToSearch) {
            logger.info("now searching: " + filename);
            ResourceData found = wikipediaResources.get(filename);
            assertNotNull("has to be there something", found);
            assertNotNull("there has to be content", found.getBinaryData());
        }
    }

    public void restWiktionaryResources() throws Exception {

        assertNotNull("for the moment this is already a test :-)", hazelcastInstance);
        logger.info("have hazelcastInstance with name: '" + hazelcastInstance.getName() + "'");

        IMap<String, ResourceData> wiktionaryResources = hazelcastInstance.getMap(WIKTIONARY_RESOURCES);
        for (String filename : wiktionaryFilesToSearch) {
            logger.info("now searching: " + filename);
            ResourceData found = wiktionaryResources.get(filename);
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
