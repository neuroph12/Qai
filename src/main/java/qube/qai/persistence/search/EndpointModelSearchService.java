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

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.main.QaiConstants;
import qube.qai.procedure.Procedure;
import qube.qai.services.SearchServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;

import java.util.ArrayList;
import java.util.Collection;

public class EndpointModelSearchService implements SearchServiceInterface, QaiConstants {

    private static Logger logger = LoggerFactory.getLogger("EndpointModelSearchService");

    private String baseUrl = "http://www.qoan.org";

    private String context;

    private String sparqlEndpoint;

    private Class objectClass;

    private ObjectRepositoryFactory factory;

    private ObjectRepository objectRepository;

    private ObjectConnection connection;

    private Repository repository;

    public EndpointModelSearchService() {
    }

    public EndpointModelSearchService(String baseUrl, String context, String sparqlEndpoint, Class objectClass) {
        this.baseUrl = baseUrl;
        this.context = context;
        this.sparqlEndpoint = sparqlEndpoint;
        this.objectClass = objectClass;
    }

    @Override
    public String getContext() {
        return context;
    }

    public void init() {

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
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        ArrayList<SearchResult> results = new ArrayList<>();

        try {

            repository.initialize();
            connection = objectRepository.getConnection();

            if (USERS.equals(fieldName)) {
                String statement = "SELECT ?s WHERE {  ?s a <http://qube.qai.user/User> }";
                Result<User> found = connection.getObjects(User.class); //Sparql.exec(model, User.class, statement);
                for (User user : found.asList()) {
                    if ("*".equals(searchString) || searchString.equals(user.getUsername())) {
                        SearchResult result = new SearchResult(USERS, user.getUsername(), user.getUuid(), "User", 1.0);
                        results.add(result);
                    }
                }
            } else if (PROCEDURES.equals(fieldName)) {
                Result<Procedure> found = connection.getObjects(Procedure.class);
                for (Procedure procedure : found.asList()) {
                    String uuid = procedure.getUuid();
                    SearchResult result = new SearchResult(PROCEDURES, procedure.getProcedureName(), uuid, procedure.getDescriptionText(), 1.0);
                    results.add(result);
                }
            } else if (USER_SESSIONS.equals(fieldName)) {
                Result<Session> found = connection.getObjects(Session.class);
                for (Session session : found.asList()) {
                    String uuid = session.getUuid();
                    SearchResult result = new SearchResult(USER_SESSIONS, session.getName(), uuid, "User session", 1.0);
                    results.add(result);
                }
            } else if (USER_ROLES.equals(fieldName)) {
                Result<Role> found = connection.getObjects(Role.class);
                for (Role role : found.asList()) {
                    String uuid = role.getUuid();
                    SearchResult result = new SearchResult(USER_ROLES, role.getName(), uuid, role.getDescription(), 1.0);
                    results.add(result);
                }
            }

            connection.close();
            repository.shutDown();

        } catch (RepositoryException e) {

            logger.error("Exception during search", e);

        } catch (QueryEvaluationException e) {

            logger.error("Exception during search", e);

        }

        return results;
    }

    public void save(Class klazz, Object data) {
        try {
            repository.initialize();
            connection = objectRepository.getConnection();
            connection.addObject(data);
            connection.commit();
            connection.close();
            repository.shutDown();
        } catch (RepositoryException e) {
            logger.error("Exception during save", e);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSparqlEndpoint() {
        return sparqlEndpoint;
    }

    public void setSparqlEndpoint(String sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    public Class getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(Class objectClass) {
        this.objectClass = objectClass;
    }
}
