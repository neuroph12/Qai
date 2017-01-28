package qube.qai.procedure.archive;

import junit.framework.TestCase;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.query.text.TextDatasetFactory;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by rainbird on 1/28/17.
 */
public class TestSparqlIndexer extends TestCase {

    private String tdbPath;

    private String lucenePath;

    private String URI;

    private String indexedProperty;

    public void testSparqlIndexer() throws Exception {

        SparqlIndexer indexer = new SparqlIndexer();

        Dataset graphDS = null;

        if( tdbPath == null )
        {
            System.out.println( "Construct an in-memory dataset" );
            graphDS = DatasetFactory.createMem();
        }
        else
        {
            System.out.println( "Construct a persistant TDB based dataset to: " + tdbPath );
            graphDS = TDBFactory.createDataset( tdbPath );
        }

        // Define the index mapping
        EntityDefinition entDef = new EntityDefinition( "uri", "text", ResourceFactory.createProperty( URI, indexedProperty ) );
        Directory luceneDir = null;

        // check for in memory or file based (persistant) index
        if( lucenePath == null )
        {
            System.out.println( "Construct an in-memory lucene index" );
            luceneDir =  new RAMDirectory();
        }
        else
        {
//            try
//            {
//                System.out.println( "Construct a persistant lucene index to: " + lucenePath );
//                //luceneDir = new SimpleFSDirectory( new File( lucenePath ) );
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
        }

        // Create new indexed dataset: Insert operations are automatically indexed with lucene
//        Dataset ds = TextDatasetFactory.createLucene( graphDS, luceneDir, entDef ) ;

//        return ds;
    }
}
