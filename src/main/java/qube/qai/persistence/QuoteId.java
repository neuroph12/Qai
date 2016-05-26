package qube.qai.persistence;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 5/15/16.
 */
public class QuoteId implements Serializable {

    private String tickerSymbol;

    private Date date;

    public QuoteId() {
    }

    public QuoteId(String tickerSymbol, Date date) {
        this.tickerSymbol = tickerSymbol;
        this.date = date;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuoteId)) return false;

        QuoteId quoteId = (QuoteId) o;

        if (getTickerSymbol() != null ? !getTickerSymbol().equals(quoteId.getTickerSymbol()) : quoteId.getTickerSymbol() != null)
            return false;
        return getDate() != null ? getDate().equals(quoteId.getDate()) : quoteId.getDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getTickerSymbol() != null ? getTickerSymbol().hashCode() : 0;
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }
}
