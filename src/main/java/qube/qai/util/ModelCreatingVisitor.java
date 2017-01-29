package qube.qai.util;

import org.apache.jena.rdf.model.Model;
import qube.qai.parsers.antimirov.nodes.*;
import qube.qai.procedure.Procedure;
import qube.qai.procedure.SelectionProcedure;
import qube.qai.procedure.SimpleProcedure;
import qube.qai.procedure.analysis.*;
import qube.qai.procedure.archive.DirectoryIndexer;
import qube.qai.procedure.archive.WikiArchiveIndexer;
import qube.qai.procedure.finance.StockEntityInitialization;
import qube.qai.procedure.finance.StockQuoteRetriever;
import qube.qai.procedure.wikiripper.WikiRipperProcedure;

/**
 * Created by rainbird on 1/28/17.
 */
public class ModelCreatingVisitor implements NodeVisitor {

    private Model model;

    @Override
    public void visit(AlternationNode node) {

    }

    @Override
    public void visit(ConcatenationNode node) {

    }

    @Override
    public void visit(EmptyNode node) {

    }

    @Override
    public void visit(IterationNode node) {

    }

    @Override
    public void visit(Node node) {

    }

    @Override
    public void visit(NameNode node) {

    }

    @Override
    public void visit(NoneNode node) {

    }

    @Override
    public void visit(PrimitiveNode node) {

    }

    public static Procedure createProcedureFromName(String name) {

        Procedure procedure = null;
        SelectionProcedure selection = new SelectionProcedure();

        if (ChangePointAnalysis.NAME.equals(name)) {
            procedure = new ChangePointAnalysis(selection);
        } else if (MarketNetworkBuilder.NAME.equals(name)) {
            procedure = new MarketNetworkBuilder(selection);
        } else if (MatrixStatistics.NAME.equals(name)) {
            procedure = new MatrixStatistics(selection);
        } else if (NetworkStatistics.NAME.equals(name)) {
            procedure = new NetworkStatistics(selection);
        } else if (NeuralNetworkAnalysis.NAME.equals(name)) {
            procedure = new NeuralNetworkAnalysis(selection);
        } else if (NeuralNetworkForwardPropagation.NAME.equals(name)) {
            procedure = new NeuralNetworkForwardPropagation(selection);
        } else if (SortingPercentilesProcedure.NAME.equals(name)) {
            procedure = new SortingPercentilesProcedure(selection);
        } else if (DirectoryIndexer.NAME.equals(name)) {
            procedure = new DirectoryIndexer(selection);
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
}
