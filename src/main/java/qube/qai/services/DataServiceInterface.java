package qube.qai.services;

import org.apache.jena.rdf.model.Model;

/**
 * Created by rainbird on 10/10/16.
 */
public interface DataServiceInterface extends SearchServiceInterface {

    /**
     * this interface is for extending the search-services, where applicable, to write
     * changes in the data out to the model, or whatever the data source might be.
     * Currently this will be mainly for employing the rdf-sources to a full extend.
     * The implementation classes will be writing the rdf-model changes either in
     * a local-directory, which will be used mainly for users, or to a remote rdf-server
     * capable of doing so. Initially, i am planning to use this for the procedures,
     * url's to their results, stock-entities and so on. i guess, there will be more later on as well.
     */
    public void save(Model model);
}
