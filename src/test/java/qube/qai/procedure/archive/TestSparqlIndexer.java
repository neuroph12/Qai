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

package qube.qai.procedure.archive;

import junit.framework.TestCase;
import org.apache.jena.query.*;
import org.apache.jena.query.text.EntityDefinition;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.sparql.util.QueryExecUtils;
import org.apache.jena.tdb.TDBFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.File;


/**
 * Created by rainbird on 1/28/17.
 */
public class TestSparqlIndexer extends TestCase {

    private String tdbPath;

    private String lucenePath;

    private String URI;

    private String indexedProperty;

    private Dataset dataset;

    public void testSparqlIndexer() throws Exception {

        SparqlIndexer indexer = new SparqlIndexer();

        fail("this test is to be done later on- right now just to have an example as to what it should be doing");

        Dataset graphDS = null;

        if (tdbPath == null) {
            System.out.println("Construct an in-memory dataset");
            graphDS = DatasetFactory.createMem();
        } else {
            System.out.println("Construct a persistant TDB based dataset to: " + tdbPath);
            graphDS = TDBFactory.createDataset(tdbPath);
        }

        // Define the index mapping
        EntityDefinition entDef = new EntityDefinition("uri", "text", ResourceFactory.createProperty(URI, indexedProperty));
        Directory luceneDir = null;

        // check for in memory or file based (persistant) index
        if (lucenePath == null) {
            System.out.println("Construct an in-memory lucene index");
            luceneDir = new RAMDirectory();
        } else {
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

        // load a ttl-file in the dataset
        File file = null;
        System.out.println("Load data ...");
        long startTime = System.currentTimeMillis();
        dataset.begin(ReadWrite.WRITE);
        try {
            Model m = dataset.getDefaultModel();
            //RDFDataMgr.read(m, file);
            dataset.commit();
        } finally {
            dataset.end();
        }

        long finishTime = System.currentTimeMillis();
        double time = finishTime - startTime;
        System.out.println("Loading finished after " + time + "ms");

    }

    // and this is how you make sparql-queries
    public void queryData(Dataset dataset) {
        System.out.println("Query data...");

        String prefix = "PREFIX ta: <" + URI + "> " +
                "PREFIX text: <http://jena.apache.org/text#> ";

        String query = "SELECT * WHERE " +
                "{ ?s text:query (ta:hasLongText 'wonderful') ." +
                "  ?s ta:hasLongText ?text . " +
                " }";

        long startTime = System.currentTimeMillis();

        dataset.begin(ReadWrite.READ);
        try {
            Query q = QueryFactory.create(prefix + query);
            QueryExecution qexec = QueryExecutionFactory.create(q, dataset);
            QueryExecUtils.executeQuery(q, qexec);
        } finally {
            dataset.end();
        }

        long finishTime = System.currentTimeMillis();

        double time = finishTime - startTime;
        System.out.println("Query finished  after " + time + "ms");

    }

    /*
     // or here is the complete example, saving us the need to search for the thing in internet again
     import java.io.File;
     import java.io.IOException;

     import org.apache.jena.query.text.EntityDefinition;
     import org.apache.jena.query.text.TextDatasetFactory;
     import org.apache.jena.query.text.TextQuery;
     import org.apache.jena.riot.RDFDataMgr;
     import org.apache.lucene.store.Directory;
     import org.apache.lucene.store.RAMDirectory;
     import org.apache.lucene.store.SimpleFSDirectory;

     import com.hp.hpl.jena.query.Dataset;
     import com.hp.hpl.jena.query.DatasetFactory;
     import com.hp.hpl.jena.query.Query;
     import com.hp.hpl.jena.query.QueryExecution;
     import com.hp.hpl.jena.query.QueryExecutionFactory;
     import com.hp.hpl.jena.query.QueryFactory;
     import com.hp.hpl.jena.query.ReadWrite;
     import com.hp.hpl.jena.rdf.model.Model;
     import com.hp.hpl.jena.rdf.model.ResourceFactory;
     import com.hp.hpl.jena.sparql.util.QueryExecUtils;
     import com.hp.hpl.jena.tdb.TDBFactory;

     public class JenaTextExample
     {
     static String URI = "http://www.tutorialacademy.com/jenatext#";

     public static void main(String ... argv)
     {
     TextQuery.init();
     Dataset ds = createIndexedDataset( "tdb", "luceneIndex", "hasLongText" );
     loadData( ds , "data.ttl" );
     queryData( ds );
     }

     public static Dataset createIndexedDataset( String tdbPath, String lucenePath, String indexedProperty )
     {
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
     try
     {
     System.out.println( "Construct a persistant lucene index to: " + lucenePath );
     luceneDir = new SimpleFSDirectory( new File( lucenePath ) );
     }
     catch (IOException e)
     {
     e.printStackTrace();
     }
     }

     // Create new indexed dataset: Insert operations are automatically indexed with lucene
     Dataset ds = TextDatasetFactory.createLucene( graphDS, luceneDir, entDef ) ;

     return ds ;
     }

     public static void loadData( Dataset dataset, String file )
     {
     System.out.println( "Load data ..." );
     long startTime = System.currentTimeMillis();
     dataset.begin( ReadWrite.WRITE );
     try
     {
     Model m = dataset.getDefaultModel();
     RDFDataMgr.read(m, file);
     dataset.commit();
     }
     finally
     {
     dataset.end();
     }

     long finishTime = System.currentTimeMillis() ;
     long time = finishTime - startTime;
     System.out.println( "Loading finished after " + time + "ms" );
     }

     public static void queryData( Dataset dataset )
     {
     System.out.println("Query data...") ;

     String prefix = "PREFIX ta: <" + URI + "> " +
     "PREFIX text: <http://jena.apache.org/text#> ";

     String query = "SELECT * WHERE " +
     "{ ?s text:query (ta:hasLongText 'wonderful') ." +
     "  ?s ta:hasLongText ?text . " +
     " }";

     long startTime = System.currentTimeMillis() ;

     dataset.begin( ReadWrite.READ ) ;
     try
     {
     Query q = QueryFactory.create( prefix + query );
     QueryExecution qexec = QueryExecutionFactory.create( q , dataset );
     QueryExecUtils.executeQuery( q, qexec );
     }
     finally
     {
     dataset.end() ;
     }

     long finishTime = System.currentTimeMillis();

     long time = finishTime - startTime;
     System.out.println( "Query finished  after " + time + "ms" );

     }

     }
     */
}
