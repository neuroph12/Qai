package qube.qai.util;

import junit.framework.TestCase;
import qube.qai.persistence.StockEntity;

import java.util.Collection;

/**
 * Created by rainbird on 1/13/17.
 */
public class TestCsvFileRipper extends TestCase {

    private String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";
    private String sNp500File = "S&P_500_constituents_financials.csv";

    public void testCsvFileRipper() throws Exception {

        CsvFileRipper ripper = new CsvFileRipper(pathToCsvFiles + sNp500File);
        Collection<StockEntity> stockEntities = ripper.ripStockEntities();
        assertNotNull("return value may not be null", stockEntities);
        assertTrue("return value may not be enpty",!stockEntities.isEmpty());

    }
}
