package qube.qai.persistence;

import qube.qai.data.AcceptsVisitors;
import qube.qai.data.DataVisitor;
import qube.qai.services.implementation.UUIDService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by rainbird on 11/19/15.
 */
@Entity
public class StockQuote implements Serializable, AcceptsVisitors {

    @EmbeddedId
    private QuoteId id;

    @Column(name = "adjustedClose")
    public double adjustedClose;

    @Column(name = "close")
    public double close;

    @Column(name = "high")
    public double high;

    @Column(name = "low")
    public double low;

    @Column(name = "open")
    public double open;

    @Column(name = "volume")
    public double volume;

    public StockQuote() {
        this.id = new QuoteId();
    }

    public StockQuote(String tickerSymbol, Date date, double adjustedClose, double close, double high, double low, double open, double volume) {
        this.id = new QuoteId(tickerSymbol, date);
        this.adjustedClose = adjustedClose;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
    }

    public StockQuote(QuoteId quoteId, double adjustedClose, double close, double high, double low, double open, double volume) {
        if (quoteId == null) {
            quoteId = new QuoteId();
        }
        this.id = quoteId;
        this.adjustedClose = adjustedClose;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
    }

    public Object accept(DataVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public Date getDate() {
        return id.getDate();
    }

    public void setDate(Date date) {
        this.id.setDate(date);
    }

    public String getTickerSymbol() {
        return id.getTickerSymbol();
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.id.setTickerSymbol(tickerSymbol);
    }

    public double getAdjustedClose() {
        return adjustedClose;
    }

    public void setAdjustedClose(double adjustedClose) {
        this.adjustedClose = adjustedClose;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public QuoteId getId() {
        return id;
    }

    public void setId(QuoteId id) {
        this.id = id;
    }
}
