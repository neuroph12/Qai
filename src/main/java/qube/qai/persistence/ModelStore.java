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

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 1/20/17.
 */
public class ModelStore implements DataServiceInterface {

    private String directoryName;

    public ModelStore() {
    }

    public ModelStore(String directoryName) {
        this.directoryName = directoryName;
    }

    public void init() {
        Dataset dataset = TDBFactory.createDataset(directoryName);

        dataset.begin(ReadWrite.READ);
        // Get model inside the transaction
        Model model = dataset.getDefaultModel();
        dataset.end();

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getDefaultModel();
        dataset.end();
    }

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
        return null;
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }

    @Override
    public void save(Model model) {

    }

    @Override
    public void remove(Model model) {

    }
}
