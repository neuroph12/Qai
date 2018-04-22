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
import qube.qai.procedure.Procedure;
import qube.qai.procedure.nodes.ValueNode;

/**
 * Created by zenpunk on 3/11/17.
 */
public class Attach extends Procedure {

    public static String NAME = "Attach Procedure";

    public static String DESCRIPTION = "Attaches two resources to each other so that their relation can be persisted ";

    public static String INPUT_ATTACH = "attach";

    public static String INPUT_ATTATCH_TO = "attachTo";

    public static String RELATION = "relation";

    private QaiDataProvider attach;

    private QaiDataProvider attachTo;

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
    public Attach() {
        super(NAME);
    }

    @Override
    public void execute() {

//        if (dataService == null) {
//            throw new RuntimeException("No DataService: Procedure has not been properly initialized- aborting");
//        }

        //Statement statement = createStatement();
        //dataService.save(statement.getModel());
        //setResultValueOf(RELATION, statement);
    }

    @Override
    public Procedure createInstance() {
        return new Attach();
    }

    /*private Statement createStatement() {

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
*/
    @Override
    public void buildArguments() {
//        arguments = new Arguments();
        getProcedureDescription().setDescription(DESCRIPTION);
        getProcedureDescription().getProcedureInputs().addInput(new ValueNode(INPUT_ATTACH, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(INPUT_ATTATCH_TO, MIMETYPE_SEARCH_RESULT));
        getProcedureDescription().getProcedureResults().addResult(new ValueNode(RELATION, MIMETYPE_STRING));
    }

    public QaiDataProvider getAttach() {
        return attach;
    }

    public void setAttach(QaiDataProvider attach) {
        this.attach = attach;
    }

    public QaiDataProvider getAttachTo() {
        return attachTo;
    }

    public void setAttachTo(QaiDataProvider attachTo) {
        this.attachTo = attachTo;
    }
}
