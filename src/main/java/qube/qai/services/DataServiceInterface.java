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

package qube.qai.services;

import org.apache.jena.rdf.model.Model;

/**
 * Created by rainbird on 10/10/16.
 */
public interface DataServiceInterface extends SearchServiceInterface {

    /**
     * this interface is for extending the search-services, where applicable, to write
     * changes in the data out to the model, or whatever the data source might be.
     * Currently this will be mainly for employing the rdf-sources to a full extend.
     * The implementation classes will be writing the rdf-model changes either in
     * a local-directory, which will be used mainly for users, or to a remote rdf-server
     * capable of doing so. Initially, i am planning to use this for the procedures,
     * url's to their results, stock-entities and so on. i guess, there will be more later on as well.
     */

    @Deprecated
    void save(Model model);

    @Deprecated
    void remove(Model model);

    @Deprecated
    Model createDefaultModel();

    void save(Class baseCLass, Object data);

    void remove(Class baseClass, Object toRemove);
}
