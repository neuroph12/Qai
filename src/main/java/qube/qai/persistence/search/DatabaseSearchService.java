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

package qube.qai.persistence.search;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.data.stores.StockQuoteDataStore;
import qube.qai.main.QaiConstants;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockGroup;
import qube.qai.persistence.StockQuote;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by rainbird on 3/5/17.
 */
public class DatabaseSearchService implements SearchServiceInterface, QaiConstants {

    private Logger logger = LoggerFactory.getLogger("DatabaseSearchService");

    private String context;

    @Inject
    private EntityManager entityManager;

    private int hitsPerPage = 0;

    public void init() {
        if (entityManager == null) {
            throw new RuntimeException("Missing EntityManager- initialization failed");
        }
        logger.info("Databease search-service: '" + context + "' has been initialized");
    }

    public DatabaseSearchService() {
    }

    public DatabaseSearchService(String context) {
        this.context = context;
    }

    public DatabaseSearchService(String context, EntityManager entityManager) {
        this.context = context;
        this.entityManager = entityManager;
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        this.hitsPerPage = hitsPerPage;

        Collection<SearchResult> results = new ArrayList<>();

        if (USERS.equals(fieldName)) {
            searchUsers(searchString, fieldName, results);
        } else if (STOCK_ENTITIES.equals(fieldName)) {
            searchStockEntitiesOfGroupWithName(searchString, fieldName, results);
        } else if (STOCK_GROUPS.equals(fieldName)) {
            searchStockGroups(searchString, fieldName, results);
        } else if (STOCK_QUOTES.equals(fieldName)) {
            searchStockQuotes(searchString, fieldName, results);
        }

        logger.info("Databease search-service: '" + context + "' brokering search: '" + searchString + "' with " + results.size() + " results");

        return results;
    }

    private void searchUsers(String searchString, String queryString, Collection<SearchResult> results) {

        String qString = "SELECT u FROM User AS u";
        Query query;
        if (StringUtils.isNoneBlank(searchString)) {
            qString += " WHERE u.username = :userName";
            query = entityManager.createQuery(qString).setParameter("userName", searchString);
        } else {
            query = entityManager.createQuery(qString);
        }

        List<User> users = query.getResultList();

        int count = 0;
        for (User user : users) {
            String idString = user.getUuid();
            SearchResult result = new SearchResult(context, user.getUsername(), idString, "User in system", 1.0);
            results.add(result);
            count++;
            if (hitsPerPage > 0 && count >= hitsPerPage) {
                break;
            }
        }
    }

    private void searchStockEntitiesOfGroupWithName(String searchString, String fieldName, Collection<SearchResult> results) {

        String qString = "SELECT s FROM StockGroup AS s WHERE s.name = :name";
        Query query = entityManager.createQuery(qString).setParameter("name", searchString);

        List<StockGroup> stockGroups = query.getResultList();

        for (StockGroup group : stockGroups) {
            Set<StockEntity> entities = group.getEntities();
            for (StockEntity entity : entities) {
                SearchResult r = new SearchResult(STOCK_ENTITIES, entity.getName(), entity.getUuid(), entity.getGicsSector(), 1.0);
                results.add(r);
            }
        }
    }

    /**
     * @param searchString in this case the name of the Stock-Group to read its children or nothing
     * @param fieldName    if STOCK_ENTITIES is used the children of the found group
     * @param results      uuid's of results
     */
    private void searchStockGroups(String searchString, String fieldName, Collection<SearchResult> results) {

        String qString = "SELECT s FROM StockGroup AS s";
        Query query = entityManager.createQuery(qString);

        List<StockGroup> stockGroups = query.getResultList();

        for (StockGroup group : stockGroups) {
            String idString = group.getUuid();
            SearchResult result = new SearchResult(STOCK_GROUPS, group.getName(), idString, group.getDescription(), 1.0);
            results.add(result);
        }
    }

    private void searchStockQuotes(String searchString, String fieldName, Collection<SearchResult> results) {

        String qString = "SELECT entity FROM StockEntity AS entity WHERE entity.tickerSymbol = :tickerSymbol";
        Query query = entityManager.createQuery(qString).setParameter("tickerSymbol", searchString);

        Collection<StockEntity> entities = query.getResultList();

        if (entities == null || entities.isEmpty()) {
            return;
        }

        for (StockEntity entity : entities) {
            Set<StockQuote> quotes = entity.getQuotes();
            if (quotes == null || quotes.isEmpty()) {
                StockQuoteDataStore store = new StockQuoteDataStore();
                String symbol = entity.getTickerSymbol().trim();
                quotes = store.retrieveQuotesFor(symbol);
                if (quotes != null && !quotes.isEmpty()) {
                    try {
                        entityManager.getTransaction().begin();
                        entity.setQuotes(quotes);
                        for (StockQuote quote : quotes) {
                            quote.setParentUUID(entity.getUuid());
                            entityManager.persist(quote);
                        }
                        entityManager.merge(entity);
                        entityManager.flush();
                        entityManager.getTransaction().commit();
                        logger.info("Added " + quotes.size() + " quotes for " + entity.getName());
                    } catch (Exception e) {
                        logger.error("Error while writing quotes of '" + symbol + "'", e);
                        entityManager.getTransaction().rollback();
                    }
                } else {
                    logger.info("Already " + quotes.size() + " quotes stored in database for " + entity.getName());
                }
            }
        }

        for (StockEntity entity : entities) {
            SearchResult r = new SearchResult(STOCK_ENTITIES, entity.getTickerSymbol(), entity.getUuid(), entity.getName(), 1.0);
            results.add(r);
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
