/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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
import org.openrdf.model.Model;
import org.openrdf.query.Dataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qube.qai.procedure.ProcedureLibraryInterface;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zenpunk on 4/19/17.
 */
public class PersistentModelMapStore implements MapStore {

    private static Logger logger = LoggerFactory.getLogger("PersistentModelMapStore");

    private String directoryName;

    private Class[] baseClasses;

    private String baseUrl = "http://www.qoan.org";

    private Model model;

    private Dataset dataset;

    //private Bean2RDF writer;

    //private RDF2Bean reader;

    @Inject
    private ProcedureLibraryInterface procedureLibrary;

    public PersistentModelMapStore(String directoryName, Class... baseClass) {
        this.baseClasses = baseClass;
        this.directoryName = directoryName;
    }

    public void init() {

        logger.info("Initializing PersistentModelMapStore for directory: " + directoryName);

        if (StringUtils.isEmpty(directoryName)) {
            String message = "'" + directoryName + "' was not found. There has to be a diretory to settle in!";
            throw new IllegalArgumentException(message);
        }

        /*dataset = TDBFactory.createDataset(directoryName);

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getNamedModel(baseUrl);
        writer = new Bean2RDF(model);
        reader = new RDF2Bean(model);

        dataset.abort();
        dataset.end();*/
    }

    @Override
    public void store(Object key, Object value) {

        logger.info("Storing object: " + value + " with key: " + key);

        /*try {

            dataset.begin(ReadWrite.WRITE);
            writer.save(baseClasses[0].cast(value));
            dataset.commit();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            dataset.end();
        }*/
    }

    @Override
    public void storeAll(Map map) {
        for (Object key : map.keySet()) {
            store(key, map.get(key));
        }
    }

    @Override
    public void delete(Object key) {

        logger.info("Deleting object with key: " + key);

        Object toDelete = load(key);

        /*try {

            dataset.begin(ReadWrite.WRITE);
            writer.delete(baseClasses[0].cast(toDelete));
            dataset.commit();

        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            dataset.end();
        }*/
    }

    @Override
    public void deleteAll(Collection keys) {
        for (Object key : keys) {
            delete(key);
        }
    }

    @Override
    public Object load(Object key) {

        logger.info("Loading object with key: " + key);

        /*try {
            dataset.begin(ReadWrite.WRITE);
            Object found = null;
            for (int i = 0; i < baseClasses.length; i++) {

                try {
                    found = reader.load(baseClasses[i], key);
                } catch (NotFoundException e) {
                    continue;
                }
                if (found != null) {
                    break;
                }
            }

            return found;

        } catch (JenaTransactionException e) {

            return null;

        } finally {

            dataset.abort();
            dataset.end();
        }*/

        return null;
    }

    /**
     * i hate having to resort to such means but
     * there really doesn't seem to be an altenrative
     * as jenabeans can't deal with sub-classes and
     * inheritance in general as it turns out
     *
     * @param key
     * @return
     */
    private Object checkProcedureTypes(Object key) {

        Object found = null;

        for (Class klass : procedureLibrary.getTemplateMap().keySet()) {

            /*try {

                found = reader.load(klass, key);

            } catch (NotFoundException e) {

                continue;
            }*/

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

}
