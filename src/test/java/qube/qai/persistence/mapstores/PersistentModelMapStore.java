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
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.utils.SelectionProcedure;
import qube.qai.procedure.utils.SimpleProcedure;
import thewebsemantic.Bean2RDF;
import thewebsemantic.NotFoundException;
import thewebsemantic.RDF2Bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainbird on 4/19/17.
 */
public class PersistentModelMapStore implements MapStore {

    private String directoryName;

    private Class baseClass;

    private String baseUrl = "http://www.qoan.org/data/";

    private Model model;

    private Dataset dataset;

    private Bean2RDF writer;

    private RDF2Bean reader;

    public PersistentModelMapStore(Class baseClass, String directoryName) {
        this.baseClass = baseClass;
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
    public void store(Object key, Object value) {
        dataset.begin(ReadWrite.WRITE);
        writer.save(baseClass.cast(value));
        dataset.commit();
        dataset.end();
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
        dataset.begin(ReadWrite.WRITE);
        writer.delete(baseClass.cast(toDelete));
        dataset.commit();
        dataset.end();
    }

    @Override
    public void deleteAll(Collection keys) {
        for (Object key : keys) {
            delete(key);
        }
    }

    @Override
    public Object load(Object key) {
        try {
            dataset.begin(ReadWrite.WRITE);
            Object found = null;
            if (baseClass.isAssignableFrom(Procedure.class)) {
                found = checkProcedureTypes(key);
            } else {
                found = reader.load(baseClass, key);
            }

            dataset.end();
            return found;
        } catch (NotFoundException e) {
            return null;
        }
    }

    private Object checkProcedureTypes(Object key) {
        Object found = null;
        for (Class klass : createClasses()) {
            try {
                found = reader.load(klass, key);
            } catch (NotFoundException e) {
                continue;
            }
            if (found != null) {
                break;
            }
        }
        return found;
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

    private static Collection<Class> createClasses() {

        Collection<Class> classes = new ArrayList<>();

        classes.add(ChangePointAnalysis.class);
        classes.add(MarketNetworkBuilder.class);
        classes.add(MatrixStatistics.class);
        classes.add(NeuralNetworkAnalysis.class);
        classes.add(NeuralNetworkForwardPropagation.class);
        classes.add(SortingPercentilesProcedure.class);
        classes.add(DirectoryIndexer.class);
        classes.add(WikiArchiveIndexer.class);
        classes.add(StockEntityInitialization.class);
        classes.add(StockQuoteRetriever.class);
        classes.add(SelectionProcedure.class);
        classes.add(SimpleProcedure.class);

        return classes;
    }
}
