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

package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import thewebsemantic.Bean2RDF;
import thewebsemantic.RDF2Bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 4/6/17.
 */
public class ModelMapStore implements MapStore {

    private Class baseClass;

    private Model model;

    private Bean2RDF writer;

    private RDF2Bean reader;

    public ModelMapStore(Class baseClass) {
        this.baseClass = baseClass;
        model = ModelFactory.createDefaultModel();
        writer = new Bean2RDF(model);
        reader = new RDF2Bean(model);
    }

    @Override
    public void store(Object key, Object value) {
        writer.save(baseClass.cast(value));
    }

    @Override
    public void storeAll(Map map) {
        for (Object key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    @Override
    public void delete(Object key) {
        Object toDelete = load(key);
        writer.delete(baseClass.cast(toDelete));
    }

    @Override
    public void deleteAll(Collection keys) {
        for (Object key : keys) {
            delete(key);
        }
    }

    @Override
    public Object load(Object key) {
        return reader.load(baseClass, key);
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
