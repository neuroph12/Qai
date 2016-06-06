package qube.qai.persistence;

import org.apache.commons.exec.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by rainbird on 6/4/16.
 */
@Embeddable
public class StockEntityId implements Serializable {

    @Column(name = "tickerSymbol", nullable = false)
    private String tickerSymbol;

    @Column(name = "tradedIn", nullable = false)
    private String tradedIn;

    public StockEntityId() {
    }

    public StockEntityId(String combinedId) {
        String[] parts = StringUtils.split(combinedId, "|");
        if (parts.length != 2) {
            throw new IllegalArgumentException("not a valid id-definition: " + combinedId);
        }
        this.tradedIn = parts[0];
        this.tickerSymbol = parts[1];
    }

    public StockEntityId(String tickerSymbol, String tradedIn) {
        this.tickerSymbol = tickerSymbol;
        this.tradedIn = tradedIn;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public String getTradedIn() {
        return tradedIn;
    }

    public void setTradedIn(String tradedIn) {
        this.tradedIn = tradedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockEntityId)) return false;

        StockEntityId that = (StockEntityId) o;

        if (!getTickerSymbol().equals(that.getTickerSymbol())) return false;
        return getTradedIn().equals(that.getTradedIn());

    }

    @Override
    public int hashCode() {
        int result = getTickerSymbol().hashCode();
        result = 31 * result + getTradedIn().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return tradedIn + "|" + tickerSymbol;
    }
}
