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

package qube.qai.procedure.utils;

import com.hazelcast.core.HazelcastInstance;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class Select extends Procedure {

    public static String NAME = "Select";

    public static String DESCRIPTION = "Select SearchResults for other procedures";

    @Inject
    private HazelcastInstance hazelcastInstance;

    private Collection<SearchResult> results;

    private Map<String, QaiDataProvider> providers;

    private Procedure provideFor;

    @Override
    public void execute() {

        /*if (providers == null) {

            providers = new ArrayList<>();
            for (SearchResult result : results) {

                QaiDataProvider provider;
                if (hazelcastInstance == null) {
                    provider = new SearchResultProvider(result);
                } else {
                    provider = new SearchResultProvider(hazelcastInstance, result);
                }
                providers.add(provider);
            }
        }*/

        if (provideFor == null) {
            throw new IllegalStateException("Select procedure has to child to provide for! Exiting");
        }


        for (String name : providers.keySet()) {
            provideFor.getProcedureDescription().getProcedureInputs().getNamedInput(name).setValue(providers.get(name));
        }

        provideFor.execute();
    }

    @Override
    public Procedure createInstance() {
        return null;
    }

    @Override
    protected void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(TARGET_COLLECTION, MIMETYPE_SEARCH_RESULT));
    }

    public Collection<SearchResult> getResults() {
        return results;
    }

    public void setResults(Collection<SearchResult> results) {
        this.results = results;
    }

    /*public Collection<QaiDataProvider> getProviders() {

        if ((results != null || !results.isEmpty())
                && (providers == null || providers.isEmpty())) {
            this.execute();
        }

        return providers;
    }

    public void fillProviders(Collection data) {
        if (providers == null) {
            providers = new ArrayList<>();
        } else {
            providers.clear();
        }

        for (Object object : data) {
            QaiDataProvider provider = new DataProvider(object);
            providers.add(provider);
        }
    }
*/
    public Procedure getProvideFor() {
        return provideFor;
    }

    public void setProvideFor(Procedure provideFor) {
        this.provideFor = provideFor;
    }

    public Map<String, QaiDataProvider> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, QaiDataProvider> providers) {
        this.providers = providers;
    }
}
