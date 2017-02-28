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

package qube.qai.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 6/23/16.
 *
 * @Deprecared i really think this is entirely obsolete
 * and i really see no reason why i should be using two different
 * libraries for the same thing rdf-models and sparql
 */
@Deprecated
public class QaiRdfHandler {

    private Logger logger = LoggerFactory.getLogger("QaiRdfHandler");

    public long count = 0;
//    private final Model model;

//    public QaiRdfHandler(Model model) {
//        this.model = model;
//    }
//
//    @Override
//    public void startRDF() throws RDFHandlerException {
//
//    }
//
//    @Override
//    public void endRDF() throws RDFHandlerException {
//
//    }
//
//    @Override
//    public void handleNamespace(String s, String s1) throws RDFHandlerException {
//        //logger.info("namespace: " + s + " " + s1);
//    }
//
//    @Override
//    public void handleStatement(Statement statement) throws RDFHandlerException {
//        //logger.info("entering: " + statement.toString());
//        Resource resource = this.model.createResource(statement.getSubject().stringValue());
//        Property property = this.model.createProperty(statement.getPredicate().toString(), statement.getPredicate().getLocalName());
//        String object = statement.getObject().stringValue();
//        org.apache.jena.rdf.model.Statement stat = model.createStatement(resource, property, object);
//        //queryExecution.execConstruct().add(stat);
//        model.add(stat);
//        count++;
//    }
//
//    @Override
//    public void handleComment(String s) throws RDFHandlerException {
//        //System.out.println("comment: " + s);
//    }

    public long getCount() {
        return count;
    }
}
