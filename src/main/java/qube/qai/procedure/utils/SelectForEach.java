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

import org.slf4j.Logger;
import qube.qai.persistence.QaiDataProvider;
import qube.qai.persistence.SearchResultProvider;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.ProcedureTemplate;
import qube.qai.procedure.nodes.ValueNode;
import qube.qai.services.ProcedureRunnerInterface;
import qube.qai.services.QaiInjectorService;
import qube.qai.services.implementation.SearchResult;

import javax.inject.Inject;
import java.util.Collection;

public class SelectForEach extends Procedure {

    @Inject
    private Logger logger;

    public static String NAME = "SelectForEach";

    public static String DESCRIPTION = "SelectForEach SearchResults for other procedures";

    @Inject
    private ProcedureRunnerInterface procedureRunner;

    private Collection<SearchResult> results;

    private String provideForFieldName;

    private ProcedureTemplate<Procedure> provideFor;

    @Override
    public void execute() {

        if (provideFor == null) {
            throw new IllegalStateException("SelectForEach procedure has to child to provide for! Exiting");
        }

        for (SearchResult result : results) {

            QaiDataProvider provider = new SearchResultProvider(result);
            Procedure procedure = provideFor.createProcedure();
            procedure.getProcedureDescription().getProcedureInputs().getNamedInput(provideForFieldName).setValue(provider);

            String message = String.format("Spawning '&s' with uuid: '%s' seeded with search context: '%s' and uuid: '%s'",
                    procedure.getProcedureName(), procedure.getUuid(), result.getContext(), result.getTitle());
            logger.info(message);

            // @TODO is there a better way of doing this?!? or better, is it really required?
            QaiInjectorService.getInstance().injectMembers(procedure);
            procedureRunner.submitProcedure(procedure);
        }


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

    public String getProvideForFieldName() {
        return provideForFieldName;
    }

    public void setProvideForFieldName(String provideForFieldName) {
        this.provideForFieldName = provideForFieldName;
    }

    public ProcedureTemplate getProvideFor() {
        return provideFor;
    }

    public void setProvideFor(ProcedureTemplate provideFor) {
        this.provideFor = provideFor;
    }
}
