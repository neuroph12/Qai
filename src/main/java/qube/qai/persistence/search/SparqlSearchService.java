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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 6/8/16.
 */
public class SparqlSearchService implements DataServiceInterface {

    /**
     * this is mainly for running Sparql-queries on remote servers.
     * the first application of which will be with the dbpedia.org server
     * later on other online databases will be added. i am hoping to use
     * this interface to query online gene- and protein-databases as well.
     */
    public SparqlSearchService() {
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
        return null;
    }

    @Override
    public void save(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void remove(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    @Override
    public Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }
}
