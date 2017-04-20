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

package qube.qai.persistence;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import qube.qai.procedure.Procedure;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.Session;
import qube.qai.user.User;
import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;
import thewebsemantic.Sparql;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 1/20/17.
 */
public class ModelStore implements DataServiceInterface {

    public static String PROCEDURES = "PROCEDURES";

    public static String USERS = "USERS";

    public static String SESSIONS = "SESSIONS";

    public static String ROLES = "ROLES";

    private String baseUrl = "http://www.qoan.org/data/";

    private boolean isSerializeObject = false;

    private String directoryName;

    private Object serializedObject;

    private Class objectClass;

    private Dataset dataset;

    private Model model;

    public ModelStore() {
    }

    public ModelStore(String directoryName) {
        this.directoryName = directoryName;
    }

    public void init() {

        if (StringUtils.isEmpty(directoryName)) {
            throw new IllegalArgumentException("there has to be a diretory to settle in!");
        }

        dataset = TDBFactory.createDataset(directoryName);

        dataset.begin(ReadWrite.READ);
        model = dataset.getNamedModel(baseUrl);
        dataset.end();
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        ArrayList<SearchResult> results = new ArrayList<>();

        dataset.begin(ReadWrite.WRITE);
        //Model model = dataset.getNamedModel(baseUrl);
        if (PROCEDURES.equals(fieldName)) {
            //Collection<Procedure> found = Sparql.exec(model, Procedure.class, searchString);
            RDF2Bean reader = new RDF2Bean(model);
            Collection<Procedure> found = reader.load(Procedure.class);
            for (Procedure procedure : found) {
                String uuid = procedure.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        } else if (USERS.equals(fieldName)) {
            RDF2Bean reader = new RDF2Bean(model);
            Collection<User> found = reader.load(User.class);
            for (User user : found) {
                if (searchString.equals(user.getUsername())) {
                    String uuid = user.getUuid();
                    SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                    results.add(result);
                }
            }
        } else if (SESSIONS.equals(fieldName)) {
            Collection<Session> found = Sparql.exec(model, Session.class, searchString);
            for (Session session : found) {
                String uuid = session.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        } else if (ROLES.equals(fieldName)) {
            Collection<Role> found = Sparql.exec(model, Role.class, searchString);
            for (Role role : found) {
                String uuid = role.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        }

        dataset.end();

        return results;
    }

    /**
     * this method converts the search result- in this case a user-resource
     * into a user-object as system knows it
     * and persists on a hazelcast map under the given id
     * so that a selector can actually broker the persisted object over hazelcast-maps
     *
     * @param objectType
     */
    private void serializeSearchResultObject(String objectType) {
        if ("user".equalsIgnoreCase(objectType)) {
            // serialize the
        }
    }

    @Override
    @Deprecated
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    @Override
    public void save(Model model) {
        dataset.begin(ReadWrite.WRITE);
        dataset.getNamedModel(baseUrl).add(model);
        dataset.commit();
        dataset.end();
    }

    @Override
    public void remove(Model model) {
        dataset.begin(ReadWrite.WRITE);
        dataset.getNamedModel(baseUrl).remove(model);
        dataset.commit();
        dataset.end();
    }

    @Override
    public Model createDefaultModel() {

//        dataset.begin(ReadWrite.WRITE);
//        Model defaultModel = dataset.getNamedModel(baseUrl);
//        dataset.end();

        return model;
    }

    @Override
    public void remove(Class baseClass, Object toRemove) {
        dataset.begin(ReadWrite.WRITE);
        //Model model = dataset.getNamedModel(baseUrl);
        Bean2RDF writer = new Bean2RDF(model);
        writer.delete(toRemove);
        dataset.end();
    }

    @Override
    public void save(Class baseCLass, Object data) {
        dataset.begin(ReadWrite.WRITE);
        //Model model = dataset.getNamedModel(baseUrl);
        Bean2RDF writer = new Bean2RDF(model);
        writer.save(data);
        dataset.end();
    }

    public Object getSerializedObject() {
        return serializedObject;
    }
}
