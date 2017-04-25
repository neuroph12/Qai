/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
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
import qube.qai.persistence.StockCategory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by rainbird on 3/5/17.
 */
public class DatabaseSearchService implements SearchServiceInterface {

    @Inject
    private EntityManager entityManager;

    private int hitsPerPage = 0;

    public void init() {
        if (entityManager == null) {
            throw new RuntimeException("Missing EntityManager- initialization failed");
        }

    }

    public DatabaseSearchService() {
    }

    public DatabaseSearchService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        this.hitsPerPage = hitsPerPage;
        String queryString = "SELECT o FROM " + fieldName + " o";
        if (StringUtils.isNoneEmpty(searchString) & !"*".equals(searchString)) {
            queryString += " WHERE " + searchString;
        }

        Collection<SearchResult> results = new ArrayList<>();

        if ("StockCategory".equals(fieldName)) {
            searchStockCategories(searchString, queryString, results);
        } else if ("User".equals(fieldName)) {
            searchUsers(searchString, queryString, results);
        }

        return results;
    }

    private void searchUsers(String searchString, String queryString, Collection<SearchResult> results) {

        Query query = entityManager.createQuery(queryString);
        List<User> users = query.getResultList();

        int count = 0;
        for (User user : users) {
            String idString = user.getUuid();
            SearchResult result = new SearchResult(searchString, idString, 1.0);
            results.add(result);
            count++;
            if (hitsPerPage > 0 && count >= hitsPerPage) {
                break;
            }
        }
    }

    private void searchStockCategories(String searchString, String queryString, Collection<SearchResult> results) {
        Query query = entityManager.createQuery(queryString);
        List<StockCategory> categories = query.getResultList();

        int count = 0;
        for (StockCategory category : categories) {
            String idString = category.getUuid();
            SearchResult result = new SearchResult(searchString, idString, 1.0);
            results.add(result);
            count++;
            if (hitsPerPage > 0 && count >= hitsPerPage) {
                break;
            }
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
