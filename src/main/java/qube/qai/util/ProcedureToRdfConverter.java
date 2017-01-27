package qube.qai.util;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import qube.qai.procedure.Procedure;
import qube.qai.services.implementation.UUIDService;

/**
 * Created by rainbird on 1/25/17.
 */
public class ProcedureToRdfConverter {

    public static Model createProcedureModel(Procedure procedure) {

        Model model = ModelFactory.createDefaultModel();

        return model;
    }

    public static Procedure createProcedureFromModel(Model model) {

        Procedure procedure = null;


        return null;
    }


}
