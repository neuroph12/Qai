package qube.qai.procedure.visitor;

import qube.qai.data.DataVisitor;
import qube.qai.network.Network;
import qube.qai.persistence.StockEntity;
import qube.qai.persistence.StockQuote;
import qube.qai.persistence.WikiArticle;
import qube.qai.procedure.Procedure;

/**
 * Created by rainbird on 12/1/15.
 */
public class SimpleProcedureVisitor implements DataVisitor {

    private StringBuffer buffer;

    public SimpleProcedureVisitor() {
        buffer = new StringBuffer();
    }

    public Object visit(StockEntity visitee, Object data) {
        buffer.append("currently visiting: ").append(visitee.getName()).append(" with idKey: ").append(visitee.getUuid()).append("\n");
        return data;
    }

    public Object visit(StockQuote visitee, Object data) {
        buffer.append("currently visiting: ").append(visitee.getTickerSymbol()).append("\n");
        return data;
    }

    public Object visit(WikiArticle visitee, Object data) {
        buffer.append("currently visiting: ").append(visitee.getTitle()).append("\n");
        return data;
    }

    public Object visit(Network visitee, Object data) {
        buffer.append("currently visiting some Network of sorts: ").append(visitee.getClass().getSimpleName()).append("\n");
        return data;
    }

    public Object visit(Procedure visitee, Object data) {
        buffer.append("currently visiting: ").append(visitee.getName()).append("\n");
        return data;
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
