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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import qube.qai.parsers.antimirov.nodes.*;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.utils.SelectionProcedure;
import qube.qai.procedure.utils.SimpleProcedure;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

/**
 * Created by rainbird on 1/28/17.
 */
public class ModelCreatingVisitor implements NodeVisitor {

    private String modelName = "procedure/";

    private String nameSpace = "file:/home/rainbird/projects/work/qai/test/"; //"http://qai.at/"; //http://www.qoan.at/" ;

    private Model model;

    public ModelCreatingVisitor(Model model) {
        this.model = model;
    }

    @Override
    public Object visit(AlternationNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(ConcatenationNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(EmptyNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(IterationNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(Node node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(NameNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(NoneNode node, Object data) {
        return allVisit(node, data);
    }

    @Override
    public Object visit(PrimitiveNode node, Object data) {
        return allVisit(node, data);
    }

    private Object allVisit(BaseNode node, Object data) {
        Resource resource = model.createResource("http://www.qai.at/procedures:" + node.getUuid());
        resource.addLiteral(model.createProperty(nameSpace, "uuid"), node.getUuid());
        resource.addLiteral(model.createProperty(nameSpace, "name"), node.getName().getName());
        resource.addLiteral(model.createProperty(nameSpace, "class"), node.getClass().getName());
        resource.addLiteral(model.createProperty(nameSpace, "hasExecuted"), ((Procedure) node).hasExecuted());
        if (node.getParent() != null) {
            resource.addLiteral(model.createProperty(nameSpace, "parentUuid"), node.getParent().getUuid());
        }
        return data;
    }

    public static BaseNode createNodeFromName(String name) {

        BaseNode procedure = null;
        SelectionProcedure selection = new SelectionProcedure();

        if (ChangePointAnalysis.NAME.equals(name)) {
            procedure = new ChangePointAnalysis(selection);
        } else if (MarketNetworkBuilder.NAME.equals(name)) {
            procedure = new MarketNetworkBuilder(selection);
        } else if (MatrixStatistics.NAME.equals(name)) {
            procedure = new MatrixStatistics();
        } else if (NetworkStatistics.NAME.equals(name)) {
            procedure = new NetworkStatistics();
        } else if (NeuralNetworkAnalysis.NAME.equals(name)) {
            procedure = new NeuralNetworkAnalysis();
        } else if (NeuralNetworkForwardPropagation.NAME.equals(name)) {
            procedure = new NeuralNetworkForwardPropagation(selection);
        } else if (SortingPercentilesProcedure.NAME.equals(name)) {
            procedure = new SortingPercentilesProcedure();
        } else if (DirectoryIndexer.NAME.equals(name)) {
            procedure = new DirectoryIndexer();
        } else if (WikiArchiveIndexer.NAME.equals(name)) {
            procedure = new WikiArchiveIndexer(selection);
        } else if (StockEntityInitialization.NAME.equals(name)) {
            procedure = new StockEntityInitialization();
        } else if (StockQuoteRetriever.NAME.equals(name)) {
            procedure = new StockQuoteRetriever();
        } else if (WikiRipperProcedure.NAME.equals(name)) {
            procedure = new WikiRipperProcedure();
        } else if (SelectionProcedure.NAME.equals(name)) {
            procedure = new SelectionProcedure();
        } else if (SimpleProcedure.NAME.equals(name)) {
            procedure = new SimpleProcedure();
        }

        return procedure;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
