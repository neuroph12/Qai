package qube.qai.procedure.finance;

import qube.qai.data.Arguments;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 1/21/17.
 */
public class StockEntityInitializationProcedure extends Procedure {

    public static String NAME = "WikiRipperProcedure";

    public static String DESCRIPTION = "Rips wiki-based archives to individual files which are easier to parse and to read";

    public static String INPUT_FILENAME  = "FILENAME";

    private static String pathToCsvFiles = "/media/rainbird/ALEPH/datasets/";

    private static String namespace = "file:///media/rainbird/ALEPH/datasets/";

    private static String S_AND_P_500_LISTING = "S&P_500_constituents_financials.csv";

    private static String OTHER_LISTED_ENTITIES = "other-listed.csv";

    private static String NASDAQ_LISTING = "nasdaq-listed.csv";

    private static String NYSE_LISTING = "nyse-listed.csv";

    private String rowPropertyName = "http://w3c/future-csv-vocab/row";

    private String selectedFile;

    @Override
    public void execute() {

    }

    @Override
    public void buildArguments() {
        name = NAME;
        description = DESCRIPTION;
        arguments = new Arguments(INPUT_FILENAME);
    }

    public String getCompleteNameSpace() {
        return namespace + selectedFile;
    }

    public static String getPathToCsvFiles() {
        return pathToCsvFiles;
    }

    public static void setPathToCsvFiles(String pathToCsvFiles) {
        StockEntityInitializationProcedure.pathToCsvFiles = pathToCsvFiles;
    }

    public String getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }

    public static String getNamespace() {
        return namespace;
    }

    public static void setNamespace(String namespace) {
        StockEntityInitializationProcedure.namespace = namespace;
    }
}
