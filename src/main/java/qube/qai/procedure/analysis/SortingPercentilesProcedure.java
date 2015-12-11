package qube.qai.procedure.analysis;

import qube.qai.data.Arguments;
import qube.qai.procedure.ProcedureChain;
import sun.font.FontRunIterator;

/**
 * Created by rainbird on 12/2/15.
 */
public class SortingPercentilesProcedure extends ProcedureChain {

    public static String NAME = "Select Procedure";

    public static String DESCRIPTION = "Selects out the specified items out of a given collection" +
            "but for the beginning, it will be taking time-series, and sorting them in order of their mean" +
            "values. top and bottom ten and the mean will be the results.";

    public SortingPercentilesProcedure() {
        super(NAME);
    }

    @Override
    public void buildArguments() {
        description = DESCRIPTION;
        arguments = new Arguments(FROM, CRITERIA);
        arguments.putResultNames(SELECTED_ITEMS);
    }

    @Override
    public void run() {

    }
}
