package qube.qai.util;

import qube.qai.persistence.StockEntity;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 1/13/17.
 */
public class CsvFileRipper {

    private String csvFilename;

    public CsvFileRipper(String csvFilename) {
        this.csvFilename = csvFilename;
    }

    public Collection<StockEntity> ripStockEntities() {
        ArrayList<StockEntity> rippedEntities = new ArrayList<>();


        return rippedEntities;
    }

    public String getCsvFilename() {
        return csvFilename;
    }

    public void setCsvFilename(String csvFilename) {
        this.csvFilename = csvFilename;
    }
}
