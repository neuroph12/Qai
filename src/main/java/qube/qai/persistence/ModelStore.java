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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static Logger logger = LoggerFactory.getLogger("ModelStore");

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

    private Bean2RDF writer;

    private RDF2Bean reader;

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

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getNamedModel(baseUrl);
        writer = new Bean2RDF(model);
        reader = new RDF2Bean(model);

        dataset.end();
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        ArrayList<SearchResult> results = new ArrayList<>();
        dataset.begin(ReadWrite.WRITE);

        if (USERS.equals(fieldName)) {
            Collection<User> found = Sparql.exec(model, User.class, "SELECT ?s WHERE { ?s a <http://qube.qai.user/User> }");
            for (User user : found) {
                if ("*".equals(searchString) || searchString.equals(user.getUsername())) {
                    SearchResult result = new SearchResult("user", user.getUuid(), 1.0);
                    results.add(result);
                }
            }
        } else if (PROCEDURES.equals(fieldName)) {
            Collection<Procedure> found = Sparql.exec(model, Procedure.class, "SELECT ?s WHERE { ?s a <http://qube.qai.procedure/Procedure> }");
            for (Procedure procedure : found) {
                String uuid = procedure.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        } else if (SESSIONS.equals(fieldName)) {
            Collection<Session> found = Sparql.exec(model, Session.class, "SELECT ?s WHERE { ?s a <http://qube.qai.user/Session> }");
            for (Session session : found) {
                String uuid = session.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        } else if (ROLES.equals(fieldName)) {
            Collection<Role> found = Sparql.exec(model, Role.class, "SELECT ?s WHERE { ?s a <http://qube.qai.user/Role> }");
            for (Role role : found) {
                String uuid = role.getUuid();
                SearchResult result = new SearchResult(fieldName, uuid, 1.0);
                results.add(result);
            }
        }

        dataset.end();

        return results;
    }

    @Override
    @Deprecated
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    @Override
    public void save(Model model) {
        dataset.begin(ReadWrite.WRITE);
        this.model.add(model);
        dataset.commit();
        dataset.end();
    }

    @Override
    public void remove(Model model) {
        dataset.begin(ReadWrite.WRITE);
        this.model.remove(model);
        dataset.commit();
        dataset.end();
    }

    @Override
    public Model createDefaultModel() {
        return model;
    }

    @Override
    public void remove(Class baseClass, Object toRemove) {
        dataset.begin(ReadWrite.WRITE);
        writer.delete(toRemove);
        dataset.commit();
        dataset.end();
    }

    @Override
    public void save(Class baseCLass, Object data) {
        dataset.begin(ReadWrite.WRITE);
        writer.save(data);
        dataset.commit();
        dataset.end();
    }

    public Object getSerializedObject() {
        return serializedObject;
    }
}
