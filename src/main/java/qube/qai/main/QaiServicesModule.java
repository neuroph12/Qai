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

package qube.qai.main;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.hazelcast.core.HazelcastInstance;
import qube.qai.persistence.search.DatabaseSearchService;
import qube.qai.persistence.search.ModelSearchService;
import qube.qai.persistence.search.MolecularResourcesSearchService;
import qube.qai.persistence.search.SimpleDirectorySearchService;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.DirectorySearchService;
import qube.qai.services.implementation.DistributedSearchListener;
import qube.qai.services.implementation.WikiSearchService;

import java.util.Properties;

public class QaiServicesModule extends AbstractModule implements QaiConstants {

    private DistributedSearchListener userSearchListener;

    private DistributedSearchListener sessionsListener;

    private DistributedSearchListener wikipediaSearchListener;

    private DistributedSearchListener wiktionarySearchListener;

    private DistributedSearchListener wikiResourcesSearchListener;

    private DistributedSearchListener stockGroupsSearchListener;

    private DistributedSearchListener stockEntitiesSearchListener;

    private DistributedSearchListener stockQuotesSearchListener;

    private DistributedSearchListener proceduresSearchListener;

    private DistributedSearchListener molecularSearchListener;

    private DistributedSearchListener pdfFileSearchListener;

    private Properties properties;

    @Override
    protected void configure() {

    }

    public QaiServicesModule(Properties properties) {
        this.properties = properties;
    }

    @Provides
    public DistributedSearchListener provideUserSearchListener(HazelcastInstance hazelcastInstance) {

        if (userSearchListener != null) {
            return userSearchListener;
        }

        SearchServiceInterface searchService = new DatabaseSearchService(USERS);
        QaiServerModule.getJpaUsersInjector().injectMembers(searchService);

        userSearchListener = new DistributedSearchListener(USERS);
        userSearchListener.setSearchService(searchService);
        userSearchListener.setHazelcastInstance(hazelcastInstance);
        userSearchListener.initialize();

        return userSearchListener;
    }

    @Provides
    public DistributedSearchListener provideSessionSearchListener(HazelcastInstance hazelcastInstance) {

        if (sessionsListener != null) {
            return sessionsListener;
        }

        SearchServiceInterface searchService = new DatabaseSearchService(USER_SESSIONS);
        QaiServerModule.getJpaUsersInjector().injectMembers(searchService);

        sessionsListener = new DistributedSearchListener(USER_SESSIONS);
        sessionsListener.setSearchService(searchService);
        sessionsListener.setHazelcastInstance(hazelcastInstance);
        sessionsListener.initialize();

        return userSearchListener;
    }

    @Provides
    public DistributedSearchListener provideWikipediaSearchListener(HazelcastInstance hazelcastInstance) {

        if (wikipediaSearchListener != null) {
            return wikipediaSearchListener;
        }

        SearchServiceInterface wikipediaSearchService = new WikiSearchService(
                WIKIPEDIA,
                properties.getProperty(WIKIPEDIA_DIRECTORY),
                properties.getProperty(WIKIPEDIA_ARCHIVE));

        ((WikiSearchService) wikipediaSearchService).initialize();

        wikipediaSearchListener = new DistributedSearchListener(WIKIPEDIA);
        wikipediaSearchListener.setSearchService(wikipediaSearchService);
        wikipediaSearchListener.setHazelcastInstance(hazelcastInstance);
        wikipediaSearchListener.initialize();

        return wikipediaSearchListener;
    }

    public DistributedSearchListener provideWiktionarySearchListener(HazelcastInstance hazelcastInstance) {

        if (wiktionarySearchListener != null) {
            return wiktionarySearchListener;
        }

        SearchServiceInterface wiktionarySearchService = new WikiSearchService(
                WIKTIONARY,
                properties.getProperty(WIKTIONARY_DIRECTORY),
                properties.getProperty(WIKTIONARY_ARCHIVE));

        ((WikiSearchService) wiktionarySearchService).initialize();

        wiktionarySearchListener = new DistributedSearchListener(WIKTIONARY);
        wiktionarySearchListener.setSearchService(wiktionarySearchService);
        wiktionarySearchListener.setHazelcastInstance(hazelcastInstance);
        wiktionarySearchListener.initialize();

        return wiktionarySearchListener;
    }

    public DistributedSearchListener provideWikiResourcesSearchListener(HazelcastInstance hazelcastInstance) {

        if (wikiResourcesSearchListener != null) {
            return wikipediaSearchListener;
        }

        SearchServiceInterface wikiResourcesSearchService = new DirectorySearchService(WIKIPEDIA_RESOURCES,
                properties.getProperty(WIKTIONARY_RESOURCE_INDEX));

        wikiResourcesSearchListener = new DistributedSearchListener(WIKIPEDIA_RESOURCES);
        wikiResourcesSearchListener.setSearchService(wikiResourcesSearchService);
        wikiResourcesSearchListener.setHazelcastInstance(hazelcastInstance);
        wikiResourcesSearchListener.initialize();

        return wikiResourcesSearchListener;
    }

    public DistributedSearchListener provideStockEntitesSearchListener(HazelcastInstance hazelcastInstance) {

        if (stockEntitiesSearchListener != null) {
            return stockEntitiesSearchListener;
        }

        SearchServiceInterface stocksSearchService = new DatabaseSearchService(STOCK_ENTITIES);
        QaiServerModule.getJpaStocksInjector().injectMembers(stocksSearchService);

        stockEntitiesSearchListener = new DistributedSearchListener(STOCK_ENTITIES);
        stockEntitiesSearchListener.setSearchService(stocksSearchService);
        stockEntitiesSearchListener.setHazelcastInstance(hazelcastInstance);
        stockEntitiesSearchListener.initialize();

        return stockEntitiesSearchListener;
    }

    public DistributedSearchListener provideStockGroupsSearchListener(HazelcastInstance hazelcastInstance) {

        if (stockGroupsSearchListener != null) {
            return stockGroupsSearchListener;
        }

        SearchServiceInterface searchService = new DatabaseSearchService(STOCK_GROUPS);
        QaiServerModule.getJpaStocksInjector().injectMembers(searchService);
        stockGroupsSearchListener = new DistributedSearchListener(STOCK_GROUPS);
        stockGroupsSearchListener.setSearchService(searchService);
        stockGroupsSearchListener.setHazelcastInstance(hazelcastInstance);
        stockGroupsSearchListener.initialize();

        return stockGroupsSearchListener;
    }

    public DistributedSearchListener provideStockQuotesSearchListener(HazelcastInstance hazelcastInstance) {

        if (stockQuotesSearchListener != null) {
            return stockQuotesSearchListener;
        }

        SearchServiceInterface searchService = new DatabaseSearchService(QaiConstants.STOCK_QUOTES);
        QaiServerModule.getJpaStocksInjector().injectMembers(searchService);

        stockQuotesSearchListener = new DistributedSearchListener(STOCK_QUOTES);
        stockQuotesSearchListener.setSearchService(searchService);
        stockQuotesSearchListener.setHazelcastInstance(hazelcastInstance);
        stockQuotesSearchListener.initialize();

        return stockQuotesSearchListener;
    }

    public DistributedSearchListener provideProcedureSearchListener(HazelcastInstance hazelcastInstance) {

        if (proceduresSearchListener != null) {
            return proceduresSearchListener;
        }

        SearchServiceInterface searchService = new ModelSearchService(PROCEDURES, properties.getProperty(PROCEDURE_BASE_DIRECTORY));
        ((ModelSearchService) searchService).init();

        proceduresSearchListener = new DistributedSearchListener(PROCEDURES);
        proceduresSearchListener.setSearchService(searchService);
        proceduresSearchListener.setHazelcastInstance(hazelcastInstance);
        proceduresSearchListener.initialize();

        return proceduresSearchListener;
    }

    public DistributedSearchListener provideMolecularSearchListener(HazelcastInstance hazelcastInstance) {

        if (molecularSearchListener != null) {
            return molecularSearchListener;
        }

        SearchServiceInterface searchService = new MolecularResourcesSearchService();

        molecularSearchListener = new DistributedSearchListener(MOLECULAR_RESOURCES);
        molecularSearchListener.setSearchService(searchService);
        molecularSearchListener.setHazelcastInstance(hazelcastInstance);
        molecularSearchListener.initialize();

        return molecularSearchListener;
    }

    public DistributedSearchListener providePdfFileSearchListener(HazelcastInstance hazelcastInstance) {

        if (pdfFileSearchListener != null) {
            return pdfFileSearchListener;
        }

        SearchServiceInterface searchService = new SimpleDirectorySearchService(PDF_FILE_RESOURCES, properties.getProperty(UPLOAD_FILE_DIRECTORY));

        pdfFileSearchListener = new DistributedSearchListener(PDF_FILE_RESOURCES);
        pdfFileSearchListener.setSearchService(searchService);
        pdfFileSearchListener.setHazelcastInstance(hazelcastInstance);
        pdfFileSearchListener.initialize();

        return pdfFileSearchListener;
    }

    public DistributedSearchListener getUserSearchListener() {
        return userSearchListener;
    }

    public void setUserSearchListener(DistributedSearchListener userSearchListener) {
        this.userSearchListener = userSearchListener;
    }

    public DistributedSearchListener getWikipediaSearchListener() {
        return wikipediaSearchListener;
    }

    public void setWikipediaSearchListener(DistributedSearchListener wikipediaSearchListener) {
        this.wikipediaSearchListener = wikipediaSearchListener;
    }

    public DistributedSearchListener getWiktionarySearchListener() {
        return wiktionarySearchListener;
    }

    public void setWiktionarySearchListener(DistributedSearchListener wiktionarySearchListener) {
        this.wiktionarySearchListener = wiktionarySearchListener;
    }

    public DistributedSearchListener getWikiResourcesSearchListener() {
        return wikiResourcesSearchListener;
    }

    public void setWikiResourcesSearchListener(DistributedSearchListener wikiResourcesSearchListener) {
        this.wikiResourcesSearchListener = wikiResourcesSearchListener;
    }

    public DistributedSearchListener getStockGroupsSearchListener() {
        return stockGroupsSearchListener;
    }

    public void setStockGroupsSearchListener(DistributedSearchListener stockGroupsSearchListener) {
        this.stockGroupsSearchListener = stockGroupsSearchListener;
    }

    public DistributedSearchListener getStockEntitiesSearchListener() {
        return stockEntitiesSearchListener;
    }

    public void setStockEntitiesSearchListener(DistributedSearchListener stockEntitiesSearchListener) {
        this.stockEntitiesSearchListener = stockEntitiesSearchListener;
    }

    public DistributedSearchListener getStockQuotesSearchListener() {
        return stockQuotesSearchListener;
    }

    public void setStockQuotesSearchListener(DistributedSearchListener stockQuotesSearchListener) {
        this.stockQuotesSearchListener = stockQuotesSearchListener;
    }

    public DistributedSearchListener getProceduresSearchListener() {
        return proceduresSearchListener;
    }

    public void setProceduresSearchListener(DistributedSearchListener proceduresSearchListener) {
        this.proceduresSearchListener = proceduresSearchListener;
    }

    public DistributedSearchListener getSessionsListener() {
        return sessionsListener;
    }

    public void setSessionsListener(DistributedSearchListener sessionsListener) {
        this.sessionsListener = sessionsListener;
    }

    public DistributedSearchListener getMolecularSearchListener() {
        return molecularSearchListener;
    }

    public void setMolecularSearchListener(DistributedSearchListener molecularSearchListener) {
        this.molecularSearchListener = molecularSearchListener;
    }

    public DistributedSearchListener getPdfFileSearchListener() {
        return pdfFileSearchListener;
    }

    public void setPdfFileSearchListener(DistributedSearchListener pdfFileSearchListener) {
        this.pdfFileSearchListener = pdfFileSearchListener;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
