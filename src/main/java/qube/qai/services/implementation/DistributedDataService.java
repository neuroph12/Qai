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

package qube.qai.services.implementation;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;

import java.io.Serializable;

/**
 * Created by rainbird on 3/5/17.
 */
public class DistributedDataService extends DistributedSearchService implements DataServiceInterface {

    private Logger logger = LoggerFactory.getLogger("DistributedDataService");

    public DistributedDataService(String searchTopicName) {
        super(searchTopicName);
    }

    public void initialize() {
        super.initialize();

        // do we have to add anything here?
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

    @Override
    public Model createDefaultModel() {
        return ModelFactory.createDefaultModel();
    }

    static public class SaveRequest implements Serializable {
        Model model;

        public SaveRequest(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            this.model = model;
        }
    }

    static public class RemoveRequest implements Serializable {
        Model model;

        public RemoveRequest(Model model) {
            this.model = model;
        }

        public Model getModel() {
            return model;
        }

        public void setModel(Model model) {
            this.model = model;
        }
    }
}
