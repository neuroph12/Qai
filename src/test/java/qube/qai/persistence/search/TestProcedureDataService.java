package qube.qai.persistence.search;

import junit.framework.TestCase;
import qube.qai.services.implementation.SearchResult;

import java.util.Collection;

/**
 * Created by rainbird on 1/25/17.
 */
public class TestProcedureDataService extends TestCase {

    public void testProcedureDataService() throws Exception {

        ProcedureDataService dataService = new ProcedureDataService();

        Collection<SearchResult> procedures = dataService.searchInputString("procedure", "*", 0);
        assertNotNull("there has to be something", procedures);
        assertTrue("the list may not be empty", !procedures.isEmpty());
    }
}
