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

package qube.qai.util;

import org.apache.jena.rdf.model.*;
import qube.qai.parsers.antimirov.nodes.NodeVisitor;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 1/25/17.
 */
public class ProcedureToRdfConverter {

    public static String baseUriString = "http://qai.at/data/procedures/";

    public static String NAME = "name";

    public static String DESCRIPTION = "description";

    public static String USERNAME = "username";

    public static String USER_UUID = "userUUID";

    public static String HAS_EXECUTED = "hasExecuted";

    public static String PROGRESS_PERCENTAGE = "progressPercentage";

    public static String DURATION = "duration";

    public static String MODEL_OUTPUT_TYPE = "RDF/XML-ABBREV";

    public Model createProcedureModel(Procedure procedure) {

        Model model = ModelFactory.createDefaultModel();

//        Resource resource = model.createResource(baseUriString + procedure.getUuid());
//        resource.addLiteral(model.createProperty(baseUriString, "name"), procedure.getNameString());
//        resource.addLiteral(model.createProperty(baseUriString, "description"), procedure.getDescription());
//        if (procedure.getUser() != null) {
//            resource.addLiteral(model.createProperty(baseUriString, "username"), procedure.getUser().getUsername());
//            resource.addLiteral(model.createProperty(baseUriString, "userUuuid"), procedure.getUser().getUuid());
//        }
//        resource.addLiteral(model.createProperty(baseUriString, "hasExecuted"), procedure.hasExecuted());
//        resource.addLiteral(model.createProperty(baseUriString, "progressPercentage"), procedure.getProgressPercentage());
//        resource.addLiteral(model.createProperty(baseUriString, "duration"), procedure.getDuration());

        NodeVisitor visitor = new ModelCreatingVisitor(model);
        procedure.childrenAccept(visitor);

        return model;
    }

//    private NodeVisitor createNodeVisitor() {
//
//        NodeVisitor visitor = new NodeVisitor() {
//
//            @Override
//            public void visit(AlternationNode node) {
//
//            }
//
//            @Override
//            public void visit(ConcatenationNode node) {
//
//            }
//
//            @Override
//            public void visit(EmptyNode node) {
//
//            }
//
//            @Override
//            public void visit(IterationNode node) {
//
//            }
//
//            @Override
//            public void visit(Node node) {
//
//            }
//
//            @Override
//            public void visit(NameNode node) {
//
//            }
//
//            @Override
//            public void visit(NoneNode node) {
//
//            }
//
//            @Override
//            public void visit(PrimitiveNode node) {
//
//            }
//        };
//
//        return visitor;
//    }

    public Procedure createProcedureFromModel(String uuid, Model model) {

        ModelCreatingVisitor visitor = new ModelCreatingVisitor(model);
        Resource resource = model.getResource(baseUriString + uuid);

        Statement statement = resource.getProperty(model.getProperty(baseUriString, NAME));
        String name = statement.getLiteral().getString();
        Procedure procedure = (Procedure) visitor.createNodeFromName(name);

        RDFVisitor rdfVisitor = createRDFVisitor();

        return procedure;
    }

    /**
     * creates the visitor which will be working the assigned values
     *
     * @return theVisitor
     */
    private RDFVisitor createRDFVisitor() {

        return new RDFVisitor() {
            @Override
            public Object visitBlank(Resource r, AnonId id) {
                return null;
            }

            @Override
            public Object visitURI(Resource r, String uri) {
                return null;
            }

            @Override
            public Object visitLiteral(Literal l) {
                return null;
            }
        };
    }
}
