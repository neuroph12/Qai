package qube.qai.procedure.finance;

import qube.qai.data.SelectorProvider;
import qube.qai.persistence.StockQuote;
import qube.qai.procedure.Procedure;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuoteRetriever extends Procedure {

    @Inject
    private SelectorProvider provider;

    public StockQuoteRetriever(String name) {
        super(name);
    }

    @Override
    public void execute() {
        Collection<StockQuote> returnValue = new ArrayList<StockQuote>();

        //
    }

    @Override
    public void buildArguments() {

    }
}
