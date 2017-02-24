package qube.qai.persistence;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

/**
 * Created by rainbird on 1/20/17.
 */
public class ModelStore {

    private String directoryName;

    public ModelStore() {
    }

    public ModelStore(String directoryName) {
        this.directoryName = directoryName;
    }

    public void init() {
        Dataset dataset = TDBFactory.createDataset(directoryName);

        dataset.begin(ReadWrite.READ);
        // Get model inside the transaction
        Model model = dataset.getDefaultModel();
        dataset.end();

        dataset.begin(ReadWrite.WRITE);
        model = dataset.getDefaultModel();
        dataset.end();
    }
}
