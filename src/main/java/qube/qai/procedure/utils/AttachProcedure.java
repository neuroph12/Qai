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

import org.apache.jena.rdf.model.Statement;
import qube.qai.data.SelectionOperator;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by rainbird on 3/11/17.
 */
public class AttachProcedure extends Procedure {

    public static String NAME = "Attach Procedure";

    public static String DESCRIPTION = "Attaches two resources to each other so that their relation can be persisted ";

    public static String INPUT_ATTACH = "attach";

    public static String INPUT_ATTATCH_TO = "attachTo";

    public static String RELATION = "relation";

    private SelectionOperator attach;

    private SelectionOperator attachTo;

//    @Inject
//    @Named("PROCEDURE")
//    private DataServiceInterface dataService;

    /**
     * this class is mainly for relating two resources with each other
     * so that their relation with each other can be persisted
     * the first example will be attaching wiki-articles to selected
     * stock-entities, and charts and plots of data which have already been
     * generated and so on.
     */
    public AttachProcedure() {
        super(NAME);
    }

    @Override
    public void execute() {

//        if (dataService == null) {
//            throw new RuntimeException("No DataService: Procedure has not been properly initialized- aborting");
//        }

        Statement statement = createStatement();
        //dataService.save(statement.getModel());
        setResultValueOf(RELATION, statement);
    }

    private Statement createStatement() {

        if (attach == null || attachTo == null) {
            throw new RuntimeException("Resources to relate are not properly defined- have to abort!");
        }

//        Model model = dataService.createDefaultModel();
//        Resource relateToResource = model.createResource(attach.toString());
//        Resource attachTo = model.createResource(relateToResource.toString());
//        Property property = model.createProperty("namespace", "relatesTo");
//        Statement statement = model.createStatement(attachTo, property, relateToResource);

        return null;
    }

    @Override
    public void buildArguments() {
//        arguments = new Arguments();
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_ATTACH, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(INPUT_ATTATCH_TO, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(RELATION, MIMETYPE_STRING));
    }

    public SelectionOperator getAttach() {
        return attach;
    }

    public void setAttach(SelectionOperator attach) {
        this.attach = attach;
    }

    public SelectionOperator getAttachTo() {
        return attachTo;
    }

    public void setAttachTo(SelectionOperator attachTo) {
        this.attachTo = attachTo;
    }

//    public DataServiceInterface getDataService() {
//        return dataService;
//    }
//
//    public void setDataService(DataServiceInterface dataService) {
//        this.dataService = dataService;
//    }
}
