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
    private Date quoetDate;

    public QuoteId() {
    }

    public QuoteId(String tickerSymbol, Date date) {
        this.tickerSymbol = tickerSymbol;
        this.quoetDate = date;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public Date getQuoetDate() {
        return quoetDate;
    }

    public void setQuoetDate(Date quoetDate) {
        this.quoetDate = quoetDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuoteId)) return false;

        QuoteId quoteId = (QuoteId) o;

        if (getTickerSymbol() != null ? !getTickerSymbol().equals(quoteId.getTickerSymbol()) : quoteId.getTickerSymbol() != null)
            return false;
        return getQuoetDate() != null ? getQuoetDate().equals(quoteId.getQuoetDate()) : quoteId.getQuoetDate() == null;

    }

    @Override
    public int hashCode() {
        int result = getTickerSymbol() != null ? getTickerSymbol().hashCode() : 0;
        result = 31 * result + (getQuoetDate() != null ? getQuoetDate().hashCode() : 0);
        return result;
    }
}
