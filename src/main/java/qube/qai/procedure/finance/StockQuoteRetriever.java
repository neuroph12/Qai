package qube.qai.procedure.finance;

import qube.qai.data.DataProvider;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.BaseProcedure;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuoteRetriever extends BaseProcedure {

    @Inject
    private DataProvider provider;

    @Override
    public void execute() {
        Collection<StockQuote> returnValue = new ArrayList<StockQuote>();

        //
    }
}
