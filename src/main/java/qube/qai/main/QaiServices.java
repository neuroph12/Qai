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

package qube.qai.main;

import qube.qai.services.implementation.DistributedSearchListener;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by rainbird on 6/29/17.
 */
public class QaiServices {

    @Inject
    @Named("Wikipedia_en")
    private DistributedSearchListener wikipediaListener;

    @Inject
    @Named("Wiktionary_en")
    private DistributedSearchListener wiktionaryListener;

    @Inject
    @Named("WikiResources_en")
    private DistributedSearchListener wikiResourcesListener;

    @Inject
    @Named("Users")
    private DistributedSearchListener usersListener;

    @Inject
    @Named("StockEntities")
    private DistributedSearchListener stockEntitiesListener;

    @Inject
    @Named("StockQuotes")
    private DistributedSearchListener stockQuotesListener;

    @Inject
    @Named("StockGroups")
    private DistributedSearchListener stockGroupsListener;

    @Inject
    @Named("Procedures")
    private DistributedSearchListener proceduresListener;

    public QaiServices() {
    }

    public void checkAllServices() {

        if (wikipediaListener == null) {
            throw new RuntimeException("Wikipedia listener missing");
        }

        if (wiktionaryListener == null) {
            throw new RuntimeException("WiktionaryListenre is missing");
        }

        if (usersListener == null) {
            throw new RuntimeException("UsersListener is missing");
        }

        if (stockEntitiesListener == null) {
            throw new RuntimeException("stockEntitiesListener is missing");
        }

        if (stockGroupsListener == null) {
            throw new RuntimeException("stockGroupsListerner is missing");
        }

        if (stockQuotesListener == null) {
            throw new RuntimeException("stockQuotesListener is missing");
        }

        if (proceduresListener == null) {
            throw new RuntimeException("procedures listener is missing");
        }
    }

    public DistributedSearchListener getWikipediaListener() {
        return wikipediaListener;
    }

    public void setWikipediaListener(DistributedSearchListener wikipediaListener) {
        this.wikipediaListener = wikipediaListener;
    }

    public DistributedSearchListener getWiktionaryListener() {
        return wiktionaryListener;
    }

    public void setWiktionaryListener(DistributedSearchListener wiktionaryListener) {
        this.wiktionaryListener = wiktionaryListener;
    }

    public DistributedSearchListener getWikiResourcesListener() {
        return wikiResourcesListener;
    }

    public void setWikiResourcesListener(DistributedSearchListener wikiResourcesListener) {
        this.wikiResourcesListener = wikiResourcesListener;
    }

    public DistributedSearchListener getUsersListener() {
        return usersListener;
    }

    public void setUsersListener(DistributedSearchListener usersListener) {
        this.usersListener = usersListener;
    }

    public DistributedSearchListener getStockEntitiesListener() {
        return stockEntitiesListener;
    }

    public void setStockEntitiesListener(DistributedSearchListener stockEntitiesListener) {
        this.stockEntitiesListener = stockEntitiesListener;
    }

    public DistributedSearchListener getStockQuotesListener() {
        return stockQuotesListener;
    }

    public void setStockQuotesListener(DistributedSearchListener stockQuotesListener) {
        this.stockQuotesListener = stockQuotesListener;
    }

    public DistributedSearchListener getStockGroupsListener() {
        return stockGroupsListener;
    }

    public void setStockGroupsListener(DistributedSearchListener stockGroupsListener) {
        this.stockGroupsListener = stockGroupsListener;
    }

    public DistributedSearchListener getProceduresListener() {
        return proceduresListener;
    }

    public void setProceduresListener(DistributedSearchListener proceduresListener) {
        this.proceduresListener = proceduresListener;
    }
}
