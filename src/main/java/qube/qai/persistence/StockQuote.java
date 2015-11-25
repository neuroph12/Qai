package qube.qai.persistence;

import java.io.Serializable;

/**
 * Created by rainbird on 11/19/15.
 */
public class StockQuote implements Serializable {

    public double adjustedClose;
    public double close;
    public double high;
    public double low;
    public double open;
    public double volume;

    public StockQuote() {
    }

    public StockQuote(double adjustedClose, double close, double high, double low, double open, double volume) {
        this.adjustedClose = adjustedClose;
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.volume = volume;
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
}
