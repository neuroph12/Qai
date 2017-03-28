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

import qube.qai.persistence.StockCategory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;

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

    public void init() {
        if (entityManager == null) {
            throw new RuntimeException("Missing EntityManager- initialization failed");
        }

    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        String queryString = "SELECT o FROM " + fieldName + " o";
        //String queryString = "SELECT o FROM StockCategory o";

//        if (StringUtils.isNoneEmpty(searchString) & !"*".equals(searchString)) {
//            queryString +=  " WHERE " + searchString;
//        }

        Query query = entityManager.createQuery(queryString);
        List<StockCategory> categories = query.getResultList();
        Collection<SearchResult> results = new ArrayList<>();
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

        return results;
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
