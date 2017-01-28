package qube.qai.util;

import org.apache.jena.rdf.model.*;
import org.apache.jena.sparql.syntax.*;
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
 * Created by rainbird on 1/25/17.
 */
public class ProcedureToRdfConverter {

    public static String baseUriString = "http://qai.at/data/procedures/";

    public static String NAME = "name";

    public static String DESCRIPTION = "descruption";

    public static String USERNAME = "username";

    public static String USER_UUID = "userUUID";

    public static String HAS_EXECUTED = "hasExecuted";

    public static String PROGRESS_PERCENTAGE = "progressPercentage";

    public static String DURATION = "duration";

    public Model createProcedureModel(Procedure procedure) {

        Model model = ModelFactory.createDefaultModel();

        Resource resource = model.createResource(baseUriString + procedure.getUuid());
        resource.addLiteral(model.createProperty(baseUriString, "name"), procedure.getNameString());
        resource.addLiteral(model.createProperty(baseUriString, "description"), procedure.getDescription());
        if (procedure.getUser() != null) {
            resource.addLiteral(model.createProperty(baseUriString, "username"), procedure.getUser().getUsername());
            resource.addLiteral(model.createProperty(baseUriString, "userUuuid"), procedure.getUser().getUuid());
        }
        resource.addLiteral(model.createProperty(baseUriString, "hasExecuted"), procedure.hasExecuted());
        resource.addLiteral(model.createProperty(baseUriString, "progressPercentage"), procedure.getProgressPercentage());
        resource.addLiteral(model.createProperty(baseUriString, "duration"), procedure.getDuration());

        return model;
    }

    public Procedure createProcedureFromModel(String uuid, Model model) {

        Resource resource = model.getResource(baseUriString + uuid);



        Statement statement = resource.getProperty(model.getProperty(baseUriString, NAME));
        String name = statement.getLiteral().getString();
        Procedure procedure = createProcedureFromName(name);

       RDFVisitor visitor = createRDFVisitor();

        return procedure;
    }

    private Procedure createProcedureFromName(String name) {

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

    private RDFVisitor createRDFVisitor() {

        RDFVisitor visitor = new RDFVisitor() {
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

        return visitor;
    }
}
