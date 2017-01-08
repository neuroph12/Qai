package qube.qai.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 5/15/16.
 */
@Embeddable
public class QuoteId implements Serializable {

    @Column(name = "tickerSymbol", nullable = false)
    private String tickerSymbol;

    @Column(name = "quoteDate", nullable = false)
    private Date quoteDate;

    public QuoteId() {
    }

    public QuoteId(String tickerSymbol, Date date) {
        this.tickerSymbol = tickerSymbol;
        this.quoteDate = date;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuoteId)) return false;

        QuoteId quoteId = (QuoteId) o;

        if (getTickerSymbol() != null ? !getTickerSymbol().equals(quoteId.getTickerSymbol()) : quoteId.getTickerSymbol() != null)
            return false;
        return getQuoteDate() != null ? getQuoteDate().equals(quoteId.getQuoteDate()) : quoteId.getQuoteDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getTickerSymbol() != null ? getTickerSymbol().hashCode() : 0;
        result = 31 * result + (getQuoteDate() != null ? getQuoteDate().hashCode() : 0);
        return result;
    }
}
