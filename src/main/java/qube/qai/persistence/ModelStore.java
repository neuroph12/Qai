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
import org.apache.jena.rdf.model.*;
import org.apache.jena.tdb.TDBFactory;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;
import qube.qai.user.Role;
import qube.qai.user.User;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 1/20/17.
 */
public class ModelStore implements DataServiceInterface {

    private String baseUrl = "http://www.qoan.org/data/";

    private boolean isSerializeObject = false;

    private String directoryName;

    private Object serializedObject;

    private Dataset dataset;

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
        Model model = dataset.getDefaultModel();
        dataset.end();
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        ArrayList<SearchResult> results = new ArrayList<>();

        dataset.begin(ReadWrite.READ);
        Model defaultModel = dataset.getDefaultModel();

        String resourceUrl = baseUrl + fieldName;
        Property property = defaultModel.createProperty(resourceUrl);
        ResIterator resIterator = defaultModel.listSubjectsWithProperty(property, searchString);
        if (resIterator != null && resIterator.hasNext()) {
            Resource resource = resIterator.next();

            User user = new User();

            StmtIterator stmtIterator = resource.listProperties();
            while (stmtIterator.hasNext()) {
                Statement statement = stmtIterator.nextStatement();
                String name = statement.getPredicate().getLocalName();
                String value = statement.getObject().asLiteral().getValue().toString();

                if ("username".equals(name)) {
                    user.setUsername(value);
                }

                if ("password".equals(name)) {
                    user.setPassword(value);
                }

                if ("uuid".equals(name)) {
                    user.setUuid(value);
                    SearchResult result = new SearchResult("user", value, 10);
                    results.add(result);
                }

                if ("roleUuid".equals(name)) {
                    Role role = new Role();
                    role.setUuid(value);
                    user.addRole(role);
                }

                if ("sessionUuid".equals(name)) {
                    user.createSession().setUuid(value);
                }
            }

            serializedObject = user;
            if (isSerializeObject) {
                serializeSearchResultObject("user");
            }

            //System.out.println("resource: " + resource.toString());
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
        dataset.getDefaultModel().add(model);
        dataset.commit();
        dataset.end();

    }

    @Override
    public void remove(Model model) {

        dataset.begin(ReadWrite.WRITE);
        dataset.getDefaultModel().remove(model);
        dataset.commit();
        dataset.end();
    }

    @Override
    public Model createDefaultModel() {

        dataset.begin(ReadWrite.WRITE);
        Model defaultModel = dataset.getDefaultModel();
        dataset.end();

        return defaultModel;
    }

    public Object getSerializedObject() {
        return serializedObject;
    }
}
