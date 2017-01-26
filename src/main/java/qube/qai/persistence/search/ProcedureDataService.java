package qube.qai.persistence.search;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import qube.qai.persistence.WikiArticle;
import qube.qai.services.DataServiceInterface;
import qube.qai.services.implementation.SearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by rainbird on 1/25/17.
 */
public class ProcedureDataService implements DataServiceInterface {

    private Dataset dataset;

    @Override
    public Collection<SearchResult> searchInputString(String searchString, String fieldName, int hitsPerPage) {
        ArrayList<SearchResult> results = new ArrayList<>();

        dataset.begin(ReadWrite.READ);
        String queryString = "";
        QueryExecution queryExec = QueryExecutionFactory.create(queryString, dataset);
        ResultSet resultSet = queryExec.execSelect();
        for (; resultSet.hasNext(); ) {
            QuerySolution solution = resultSet.next();
            for (Iterator<String> it = solution.varNames(); it.hasNext(); ) {
                String name = it.next();

                // don't forget to add the things to the return list
                // after converting the thing- obviously
            }
        }

        return results;
    }

    @Override
    public void save(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public void update(Model model) {
        throw new RuntimeException("method not implemented");
    }

    @Override
    public WikiArticle retrieveDocumentContentFromZipFile(String fileName) {
        return null;
    }
}
