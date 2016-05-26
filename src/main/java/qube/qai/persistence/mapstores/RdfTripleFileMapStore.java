package qube.qai.persistence.mapstores;

import com.hazelcast.core.MapStore;
import qube.qai.persistence.RDFTriple;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rainbird on 5/24/16.
 */
public class RdfTripleFileMapStore implements MapStore<RDFTriple.RDFKey, RDFTriple> {

    private String rdfTurtleFile;

    @Inject
    private EntityManager entityManager;

    public RdfTripleFileMapStore() {
    }

    public RdfTripleFileMapStore(String rdfTurtleFile) {
        this.rdfTurtleFile = rdfTurtleFile;
    }

    @Override
    public void store(RDFTriple.RDFKey key, RDFTriple value) {

    }

    @Override
    public void storeAll(Map<RDFTriple.RDFKey, RDFTriple> map) {

    }

    @Override
    public void delete(RDFTriple.RDFKey key) {

    }

    @Override
    public void deleteAll(Collection<RDFTriple.RDFKey> keys) {

    }

    @Override
    public RDFTriple load(RDFTriple.RDFKey key) {
        return null;
    }

    @Override
    public Map<RDFTriple.RDFKey, RDFTriple> loadAll(Collection<RDFTriple.RDFKey> keys) {
        return null;
    }

    @Override
    public Iterable<RDFTriple.RDFKey> loadAllKeys() {
        return null;
    }

    public String getRdfTurtleFile() {
        return rdfTurtleFile;
    }

    public void setRdfTurtleFile(String rdfTurtleFile) {
        this.rdfTurtleFile = rdfTurtleFile;
    }
}
