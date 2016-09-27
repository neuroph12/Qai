package qube.qai.persistence;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rainbird on 6/23/16.
 */
public class QaiRdfHandler implements RDFHandler {

    private Logger logger = LoggerFactory.getLogger("QaiRdfHandler");

    public long count = 0;
    private final Model model;

    public QaiRdfHandler(Model model) {
        this.model = model;
    }

    @Override
    public void startRDF() throws RDFHandlerException {

    }

    @Override
    public void endRDF() throws RDFHandlerException {

    }

    @Override
    public void handleNamespace(String s, String s1) throws RDFHandlerException {
        //logger.info("namespace: " + s + " " + s1);
    }

    @Override
    public void handleStatement(Statement statement) throws RDFHandlerException {
        //logger.info("entering: " + statement.toString());
        Resource resource = this.model.createResource(statement.getSubject().stringValue());
        Property property = this.model.createProperty(statement.getPredicate().toString(), statement.getPredicate().getLocalName());
        String object = statement.getObject().stringValue();
        org.apache.jena.rdf.model.Statement stat = model.createStatement(resource, property, object);
        //queryExecution.execConstruct().add(stat);
        model.add(stat);
        count++;
    }

    @Override
    public void handleComment(String s) throws RDFHandlerException {
        //System.out.println("comment: " + s);
    }

    public long getCount() {
        return count;
    }
}
