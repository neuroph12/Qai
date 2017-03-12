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

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 1/25/17.
 */
public class ProcedureDataService implements DataServiceInterface {

    private Dataset dataset;

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {

        ArrayList<SearchResult> results = new ArrayList<>();

        dataset.begin(ReadWrite.READ);
        String queryString = "select * from " + fieldName + " where " + searchString;
        QueryExecution queryExec = QueryExecutionFactory.create(queryString, dataset);
        ResultSet resultSet = queryExec.execSelect();
        for (; resultSet.hasNext(); ) {
            QuerySolution solution = resultSet.next();
            String uuid = solution.getResource("uuid").toString();
            // don't forget to add the things to the return list
            // after converting the thing- obviously
            SearchResult result = new SearchResult(fieldName, uuid, 10.0);
            results.add(result);
        }

        dataset.end();

        return results;
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
        dataset.close();

        return defaultModel;
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
