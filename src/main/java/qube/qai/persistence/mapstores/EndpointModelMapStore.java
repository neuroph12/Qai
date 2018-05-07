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

import com.hazelcast.core.MapStore;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EndpointModelMapStore implements MapStore {

    private static Logger logger = LoggerFactory.getLogger("ModelMapStore");

    private Class baseClass;

    private Repository repository;

    private ObjectRepository objectRepository;

    private String sparqlEndpoint;

    private String urlString;

    public EndpointModelMapStore() {
    }

    public EndpointModelMapStore(Class baseClass, String sparqlEndpoint, String urlString) {
        this.baseClass = baseClass;
        this.sparqlEndpoint = sparqlEndpoint;
        this.urlString = urlString;
    }

    public void initialize() {

        try {
            repository = new SPARQLRepository(sparqlEndpoint);
            repository.initialize();
            ObjectRepositoryFactory factory = new ObjectRepositoryFactory();
            objectRepository = factory.createRepository(repository);
        } catch (RepositoryException e) {
            logger.error("Exception while initialization", e);
        } catch (RepositoryConfigException e) {
            logger.error("Exception while initialization", e);
        }

    }

    @Override
    public void store(Object key, Object value) {

        try {

            ObjectConnection connection = objectRepository.getConnection();

            ValueFactory vf = connection.getValueFactory();
            URI id = vf.createURI(urlString, key.toString());

            connection.addObject(id, value);

            connection.commit();

        } catch (RepositoryException e) {
            logger.error("Exception while store", e);
        }
    }

    @Override
    public void storeAll(Map map) {
        for (Object key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    @Override
    public void delete(Object key) {
        try {

            ObjectConnection connection = objectRepository.getConnection();

            ValueFactory vf = connection.getValueFactory();
            URI id = vf.createURI(urlString, key.toString());
            Object value = connection.getObject(id);
            if (value != null) {
                connection.removeDesignation(value, id);
            }

            connection.commit();

        } catch (RepositoryException e) {
            logger.error("Exception while delete", e);
        }
    }

    @Override
    public void deleteAll(Collection keys) {
        for (Object key : keys) {
            delete(key);
        }
    }

    @Override
    public Object load(Object key) {
        Object value = null;
        try {
            repository.initialize();
            ObjectConnection connection = objectRepository.getConnection();

            ValueFactory vf = connection.getValueFactory();
            URI id = vf.createURI(urlString, key.toString());
            value = connection.getObject(id);

            connection.commit();

        } catch (RepositoryException e) {
            logger.error("Exception while load", e);
        } finally {
            return value;
        }
    }

    @Override
    public Map loadAll(Collection keys) {
        Map results = new HashMap();
        for (Object key : keys) {
            Object object = load(key);
            results.put(key, object);
        }
        return results;
    }

    @Override
    public Iterable loadAllKeys() {
        return null;
    }
}
