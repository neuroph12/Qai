package qube.qai.data;

import qube.qai.network.Network;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 12/27/15.
 */
public interface DataVisitor {

    Object visit(StockEntity visitee, Object data);

    Object visit(StockQuote visitee, Object data);

    Object visit(WikiArticle visitee, Object data);

    Object visit(Network visitee, Object data);

    Object visit(Procedure visitee, Object data);
}
