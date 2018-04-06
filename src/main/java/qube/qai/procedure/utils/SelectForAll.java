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

import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.SearchResultProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.procedure.SpawningProcedure;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rainbird on 12/27/15.
 */
public class SelectForAll extends Procedure implements SpawningProcedure {

    public String NAME = "SelectionForAll";

    public String DESCRIPTION = "Selects the data input for other procedures";

    public static String PARAMEETER_ASSIIGN_TO = "ASSIGN_TO";

    public static String PARAMEETER_ASSIIGN_FROM = "ASSIGN_FROM";

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    private Collection<SearchResult> results;

    private String provideForFieldName;

    private ProcedureTemplate template;

    private Collection<Procedure> spawn;

    private boolean executeImmediately;

    /**
     * this is mainly to pass the children the argument
     * represents user preparing, or choosing a certain
     * input for the children to process
     */
    public SelectForAll() {
        super("SelectionForAll");
    }

    @Override
    public void buildArguments() {
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(PARAMEETER_ASSIIGN_TO, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(PARAMEETER_ASSIIGN_FROM, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(POINTER_OR_DATA_VALUE, MIMETYPE_PROCEDURE));
    }

    @Override
    public void execute() {

        if (template == null) {
            throw new IllegalStateException("SelectForEach procedure has to child to provide for! Exiting");
        }

        if (results == null || results.isEmpty()) {
            info("There are no inputs- terminating execution");
            return;
        }

        spawn = new ArrayList<>();

        Procedure procedure = template.createProcedure();
        spawn.add(procedure);

        for (SearchResult result : results) {
            QaiDataProvider provider = new SearchResultProvider(result);
            procedure.addInputs(provider);
        }

        if (executeImmediately) {

            QaiInjectorService.getInstance().injectMembers(procedure);
            procedure.execute();

        } else {

            procedureRunner.submitProcedure(procedure);

        }

    }

    @Override
    public boolean haveChildrenExceuted() {

        boolean allExecuted = true;

        for (Procedure procedure : spawn) {
            allExecuted &= procedure.hasExecuted();
        }

        return allExecuted;
    }

    @Override
    public Set<String> getSpawnedProcedureUUIDs() {

        Set<String> uuids = new HashSet<>();
        for (Procedure procedure : spawn) {
            uuids.add(procedure.getUuid());
        }

        return uuids;
    }

    @Override
    public Procedure createInstance() {
        return new SelectForAll();
    }

    public Collection<SearchResult> getResults() {
        return results;
    }

    public void setResults(Collection<SearchResult> results) {
        this.results = results;
    }

    public String getProvideForFieldName() {
        return provideForFieldName;
    }

    public void setProvideForFieldName(String provideForFieldName) {
        this.provideForFieldName = provideForFieldName;
    }

    public ProcedureTemplate<Procedure> getTemplate() {
        return template;
    }

    public void setTemplate(ProcedureTemplate template) {
        this.template = template;
    }

    public Collection<Procedure> getSpawn() {
        return spawn;
    }

    public void setSpawn(Collection<Procedure> spawn) {
        this.spawn = spawn;
    }

    public void setExecuteImmediately(boolean executeImmediately) {
        this.executeImmediately = executeImmediately;
    }
}
