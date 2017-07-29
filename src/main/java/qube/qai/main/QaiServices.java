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

import com.google.inject.Injector;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.services.implementation.DistributedSearchListener;

import java.util.Collection;
import java.util.Properties;

import static qube.qai.main.QaiConstants.*;

/**
 * Created by rainbird on 6/29/17.
 */
public class QaiServices {

    private static Logger logger = LoggerFactory.getLogger("QaiServices");

    private DistributedSearchListener wikipediaListener;

    private DistributedSearchListener wiktionaryListener;

    private DistributedSearchListener wikiResourcesListener;

    private DistributedSearchListener usersListener;

    private DistributedSearchListener stockEntitiesListener;

    private DistributedSearchListener stockQuotesListener;

    private DistributedSearchListener stockGroupsListener;

    private DistributedSearchListener proceduresListener;

    public QaiServices() {
    }

    public void startServices(Injector injector, Properties properties, Collection<String> localServices) {

        String message = "Initilailizing service: %s";

        QaiServicesModule qaiServices = new QaiServicesModule(properties);
        HazelcastInstance hazelcastInstance = injector.getInstance(HazelcastInstance.class);

        if (localServices.contains(USERS)) {
            logger.info(String.format(message, USERS));
            usersListener = qaiServices.provideUserSearchListener(hazelcastInstance);
        }

        if (localServices.contains(PROCEDURES)) {
            logger.info(String.format(message, PROCEDURES));
            proceduresListener = qaiServices.provideProcedureSearchListener(hazelcastInstance);
        }

        if (localServices.contains(WIKIPEDIA)) {
            logger.info(String.format(message, WIKIPEDIA));
            wikipediaListener = qaiServices.provideWikipediaSearchListener(hazelcastInstance);
        }

        if (localServices.contains(WIKTIONARY)) {
            logger.info(String.format(message, WIKTIONARY));
            wiktionaryListener = qaiServices.provideWiktionarySearchListener(hazelcastInstance);
        }

        if (localServices.contains(WIKIPEDIA_RESOURCES)) {
            logger.info(String.format(message, WIKIPEDIA_RESOURCES));
            wikiResourcesListener = qaiServices.provideWikiResourcesSearchListener(hazelcastInstance);
        }

        if (localServices.contains(STOCK_GROUPS)) {
            logger.info(String.format(message, STOCK_GROUPS));
            stockGroupsListener = qaiServices.provideStockGroupsSearchListener(hazelcastInstance);
        }

        if (localServices.contains(STOCK_ENTITIES)) {
            logger.info(String.format(message, STOCK_ENTITIES));
            stockEntitiesListener = qaiServices.provideStockEntitesSearchListener(hazelcastInstance);
        }

        if (localServices.contains(STOCK_QUOTES)) {
            logger.info(String.format(message, STOCK_QUOTES));
            stockQuotesListener = qaiServices.provideStockQuotesSearchListener(hazelcastInstance);
        }
    }

    public void checkAllServices() {

        String messageOn = "SearchListener: '%s' has been started";
        String messageOff = "SearchListener: '%s' has not been started";

        if (usersListener != null) {
            logger.info(String.format(messageOn, USERS));
        } else {
            logger.info(String.format(messageOff, USERS));
        }

        if (proceduresListener != null) {
            logger.info(String.format(messageOn, PROCEDURES));
        } else {
            logger.info(String.format(messageOff, PROCEDURES));
        }

        if (wikipediaListener != null) {
            logger.info(String.format(messageOn, WIKIPEDIA));
        } else {
            logger.info(String.format(messageOff, WIKIPEDIA));
        }

        if (wiktionaryListener != null) {
            logger.info(String.format(messageOn, WIKTIONARY));
        } else {
            logger.info(String.format(messageOff, WIKTIONARY));
        }

        if (wikiResourcesListener != null) {
            logger.info(String.format(messageOn, WIKIPEDIA_RESOURCES));
        } else {
            logger.info(String.format(messageOff, WIKIPEDIA_RESOURCES));
        }

        if (stockGroupsListener != null) {
            logger.info(String.format(messageOn, STOCK_GROUPS));
        } else {
            logger.info(String.format(messageOff, STOCK_GROUPS));
        }

        if (stockEntitiesListener != null) {
            logger.info(String.format(messageOn, STOCK_ENTITIES));
        } else {
            logger.info(String.format(messageOff, STOCK_ENTITIES));
        }

        if (stockQuotesListener != null) {
            logger.info(String.format(messageOn, STOCK_QUOTES));
        } else {
            logger.info(String.format(messageOff, STOCK_QUOTES));
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
